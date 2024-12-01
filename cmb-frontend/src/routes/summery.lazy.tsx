import {
  createLazyFileRoute,
  useSearch,
  useNavigate,
} from "@tanstack/react-router";
import {  useQuery } from "@tanstack/react-query";
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
  const navigate = useNavigate();
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
  const [selectedMonth, setSelectedMonth] = useState("");
  const [selectedYear, setSelectedYear] = useState("");
  const [salaryDate, setSalaryDate] = useState("");

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

  const handleApplyFilters = () => {
    if (selectedMonth && selectedYear && salaryDate) {
      const month = parseInt(selectedMonth, 10);
      const year = parseInt(selectedYear, 10);

      const start = [
        year,
        String(month - 1).padStart(2, "0"),
        String(parseInt(salaryDate, 10)).padStart(2, "0"),
      ].join("-");
      const end = [
        year,
        String(month).padStart(2, "0"),
        String(parseInt(salaryDate, 10)).padStart(2, "0"),
      ].join("-");

      navigate({
        to: "/summery",
        search: (old: any) => ({ ...old, start, end }),
      });
    }
  };

  const handleNextMonth = () => {
    if (selectedMonth && selectedYear) {
      let month = parseInt(selectedMonth, 10);
      let year = parseInt(selectedYear, 10);

      if (month === 12) {
        month = 1;
        year += 1;
      } else {
        month += 1;
      }

      setSelectedMonth(month.toString());
      setSelectedYear(year.toString());
      handleApplyFilters();
    }
  };

  const handlePreviousMonth = () => {
    if (selectedMonth && selectedYear) {
      let month = parseInt(selectedMonth, 10);
      let year = parseInt(selectedYear, 10);

      if (month === 1) {
        month = 12;
        year -= 1;
      } else {
        month -= 1;
      }

      setSelectedMonth(month.toString());
      setSelectedYear(year.toString());
      handleApplyFilters();
    }
  };

  const months = [
    { value: "1", label: "January" },
    { value: "2", label: "February" },
    { value: "3", label: "March" },
    { value: "4", label: "April" },
    { value: "5", label: "May" },
    { value: "6", label: "June" },
    { value: "7", label: "July" },
    { value: "8", label: "August" },
    { value: "9", label: "September" },
    { value: "10", label: "October" },
    { value: "11", label: "November" },
    { value: "12", label: "December" },
  ];

  const years = Array.from({ length: 20 }, (_, i) => {
    const year = new Date().getFullYear() - i;
    return { value: year.toString(), label: year.toString() };
  });

  return (
    <div className="p-4 h-screen w-screen">
      <div className="flex flex-row h-full">
        <div className="w-1/2 h-full flex flex-col">
          <h2 className="font-bold">
            Debit Summary - Total: ~{" "}
            {summeryQuery.data
              ? Math.round(
                  summeryQuery.data.summery.reduce(
                    (accumulator: any, currentValue: any) =>
                      accumulator + currentValue.totalDebit,
                    0
                  )
                )
              : null}{" "}
            Rs
          </h2>
          <div className="flex flex-grow flex-shrink-0 p-4">
            {data ? (
              <DebitSummaryPieChart
                summaryData={data.summery}
                onSelect={(v: any) => {
                  setTag(v.defaultTag);
                }}
              />
            ) : (
              "Loading..."
            )}
          </div>
          <div className="mb-4 flex flex-row items-center gap-2">
            <select
              value={selectedMonth}
              onChange={(e) => setSelectedMonth(e.target.value)}
              className="border p-1"
            >
              <option value="">Month</option>
              {months.map((month) => (
                <option key={month.value} value={month.value}>
                  {month.label}
                </option>
              ))}
            </select>

            <select
              value={selectedYear}
              onChange={(e) => setSelectedYear(e.target.value)}
              className="border p-1"
            >
              <option value="">Year</option>
              {years.map((year) => (
                <option key={year.value} value={year.value}>
                  {year.label}
                </option>
              ))}
            </select>

            <input
              type="number"
              value={salaryDate}
              onChange={(e) => setSalaryDate(e.target.value)}
              placeholder="Salary Date (DD)"
              className="border p-1"
            />

            <button
              onClick={handleApplyFilters}
              className="p-2 bg-blue-500 text-white"
            >
              Apply
            </button>
            <button
              onClick={handlePreviousMonth}
              className="p-2 bg-gray-500 text-white"
            >
              Previous Month
            </button>
            <button
              onClick={handleNextMonth}
              className="p-2 bg-gray-500 text-white"
            >
              Next Month
            </button>
          </div>
        </div>
        <div className="w-1/2 h-full">
          <div className="px-4 pb-3 h-full overflow-scroll">
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
    onClick: (_: any, elements: any) => {
      onSelect?.(summaryData[elements[0].index]);
    },
  };

  useEffect(() => {
    if (summaryData) {
      const labels = summaryData.map(
        (item: any) =>
          `${capitalizeFirstLetter(item.defaultTag || "No Tag")}: ${item.totalDebit} (Transactions: ${item.totalTransactions})`
      );
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

  return <Pie data={chartData} options={options} {...rest} />;
};
