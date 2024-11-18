import { createLazyFileRoute } from "@tanstack/react-router";
import TransactionsTable from "../components/transactions-table";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { getTransactions } from "../api/auth";
import { useAuthStore } from "../store/auth";
import LiveBadge from "../components/status-badge";
import { useTransactionUpdates } from "../helper/websocket";
import { useEffect } from "react";

export const Route = createLazyFileRoute("/transactions")({
  component: RouteComponent,
});

function RouteComponent() {
  const askNotificationPermissions = () => {
    Notification.requestPermission();
  };

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

  useTransactionUpdates((update) => {
    update = JSON.parse(update);
    const u1 = update?.newTransactions.length > 0 ? update.newTransactions[0].description : "<no description>";

    const text = `New ${update.newLogs} transactions\n${u1} and maybe more...`;
    new Notification("New transactions", { body: text });
    qc.invalidateQueries({ queryKey: ["transaction"] });
  }, accessToken);

  const transactions = transactionsQuery.data;

  return (
    <div>
      <div className="flex flex-row w-screen justify-between p-6">
        <h2 className="text-2xl font-bold text-gray-700 flex gap-4">
          <LiveBadge /> Transactions{" "}
        </h2>
        <button
          onClick={askNotificationPermissions}
          className="px-4 py-1 bg-gray-500 text-white font-semibold rounded-md hover:bg-gray-600 focus:outline-none focus:ring-2 focus:ring-gray-400 focus:ring-offset-2 transition"
        >
          Get notifications
        </button>
      </div>
      <div className="px-4 pb-3">
        {transactions ? (
          <TransactionsTable transactions={transactions.transactionList} />
        ) : null}
      </div>
    </div>
  );
}
