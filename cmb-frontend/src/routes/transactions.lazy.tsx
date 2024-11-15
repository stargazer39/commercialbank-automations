import * as React from "react";
import { createLazyFileRoute } from "@tanstack/react-router";
import TransactionsTable from "../components/transactions-table";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { getTransactions } from "../api/auth";
import { useAuthStore } from "../store/auth";
import LiveBadge from "../components/status-badge";
import { useTransactionUpdates } from "../helper/websocket";

export const Route = createLazyFileRoute("/transactions")({
  component: RouteComponent,
});

function RouteComponent() {
  const accessToken = useAuthStore((state) => state.accessToken);

  const transactionsQuery = useQuery({
    queryKey: ["transaction", { page: 1, size: 100 }],
    queryFn: (q) => {
      console.log("queryFn", q);
      return getTransactions({
        accessToken,
        page: 1,
        size: 100,
      });
    },
  });

  const qc = useQueryClient();

  useTransactionUpdates(() => {
    qc.invalidateQueries({ queryKey: ["transaction"] });
  }, accessToken);

  const transactions = transactionsQuery.data;

  return (
    <div>
      <h2 className="text-2xl font-bold text-gray-700 p-6 flex gap-4">
        <LiveBadge /> Transactions
      </h2>
      <div className="px-4 pb-3">
        {transactions ? (
          <TransactionsTable transactions={transactions.transactionList} />
        ) : null}
      </div>
    </div>
  );
}
