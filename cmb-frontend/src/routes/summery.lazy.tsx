import {
  createLazyFileRoute,
  useParams,
  useSearch,
} from "@tanstack/react-router";
import { useMutation, useQuery } from "@tanstack/react-query";
import { useAuthStore } from "../store/auth";
import { getSummery, getTransactions } from "../api/auth";
import { Pie } from "react-chartjs-2";
import { useEffect, useState } from "react";
import "chart.js/auto";
import { capitalizeFirstLetter } from "../helper/text";
import TransactionsTable from "../components/transactions-table";

export const Route = createLazyFileRoute("/summery")({
  component: RouteComponent,
});

function RouteComponent() {
  const query = useSearch({ strict: false });
  const authStore = useAuthStore();
  const summeryQuery = useQuery({
    queryKey: ["summery", query],
    queryFn: (q) => {
      console.log("queryFn", q);
      return getSummery({
        accessToken: authStore.accessToken,
        ...q.queryKey[1],
      });
    },
  });

  const data = summeryQuery.data;

  const [tag, setTag] = useState<string | null>(null);

  const transactionsQuery = useQuery({
    queryKey: [
      "transaction",
      {
        page: 1,
        size: 100,
        tag,
        start: query.start,
        end: query.end,
        type: "DEBIT",
      },
    ],
    queryFn: (q) => {
      console.log("queryFn", q);
      return getTransactions({
        accessToken: authStore.accessToken,
        ...(q.queryKey[1] as any),
      });
    },
  });

  const transactions = transactionsQuery.data;

  return (
    <div className="p-4 h-screen w-screen">
      <div className="flex flex-row">
        {data ? (
          <DebitSummaryPieChart
            summaryData={data.summery}
            onSelect={(v: any) => {
              setTag(v.defaultTag);
            }}
            className="w-1/2"
          />
        ) : (
          "Loading..."
        )}
        <div className="w-1/2">
          <div className="px-4 pb-3">
            {transactions ? (
              <TransactionsTable transactions={transactions.transactionList} />
            ) : null}
          </div>
        </div>
      </div>
    </div>
  );
}

const DebitSummaryPieChart = ({ summaryData, onSelect, ...rest }: any) => {
  const [chartData, setChartData] = useState({
    labels: [],
    datasets: [
      {
        data: [],
        backgroundColor: [
          "rgba(255, 99, 132, 0.2)",
          "rgba(54, 162, 235, 0.2)",
          "rgba(255, 206, 86, 0.2)",
          "rgba(75, 192, 192, 0.2)",
          "rgba(153, 102, 255, 0.2)",
          "rgba(255, 159, 64, 0.2)",
        ],
        borderColor: [
          "rgba(255, 99, 132, 1)",
          "rgba(54, 162, 235, 1)",
          "rgba(255, 206, 86, 1)",
          "rgba(75, 192, 192, 1)",
          "rgba(153, 102, 255, 1)",
          "rgba(255, 159, 64, 1)",
        ],
        borderWidth: 1,
      },
    ],
  });

  const options: any = {
    responsive: true,
    plugins: {
      legend: {
        display: true,
      },
    },
    onClick: (event: any, elements: any) => {
      onSelect?.(summaryData[elements[0].index]);
    },
  };

  useEffect(() => {
    if (summaryData) {
      const labels = summaryData.map((item: any) => [
        `${capitalizeFirstLetter(item.defaultTag || "No Tag")}: ${item.totalDebit} (Transactions: ${item.totalTransactions})`,
        1,
      ]);
      const data = summaryData.map((item: any) => item.totalDebit);

      setChartData((prevData) => ({
        ...prevData,
        labels,
        datasets: [
          {
            ...prevData.datasets[0],
            data,
          },
        ],
      }));
    }
  }, [summaryData]);

  return (
    <div {...rest}>
      <h2 className="font-bold">Debit Summary</h2>
      <Pie data={chartData} options={options} />
    </div>
  );
};
