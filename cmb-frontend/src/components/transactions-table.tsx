import { Transaction } from "../api/auth";
import { humanReadable } from "../helper/time";

const TransactionRow = ({ transaction }: { transaction: Transaction }) => {
  const rowItemclass = "px-4 py-2 border-b text-center";
  return (
    <tr className={`${transaction.credit ? "bg-green-200":"bg-red-200"}`}>
      {/* <td className="px-4 py-2 border-b">{transaction.hash}</td> */}
      <td className={rowItemclass}>{humanReadable(transaction.createdAt)}</td>
      {/* <td className={rowItemclass}>{transaction.userId}</td> */}
      <td className={rowItemclass}>{transaction.transactionDate}</td>
      <td className={rowItemclass}>{transaction.description}</td>
      <td className={rowItemclass}>{transaction.currency}</td>
      <td className={rowItemclass}>{transaction.debit}</td>
      <td className={rowItemclass}>{transaction.credit}</td>
      <td className={rowItemclass}>{transaction.runningBalance}</td>
      <td className={rowItemclass}>{transaction.accountNumber}</td>
    </tr>
  );
};

const TransactionsTable = ({
  transactions,
}: {
  transactions: Transaction[];
}) => {
  return (
    <table className="w-full table-auto border-collapse text-sm">
      <thead>
        <tr className="bg-gray-200">
          {/* <th className="px-4 py-2 border-b">Hash</th> */}
          <th className="px-4 py-2 border-b">Created At</th>
          {/* <th className="px-4 py-2 border-b">User ID</th> */}
          <th className="px-4 py-2 border-b">Transaction Date</th>
          <th className="px-4 py-2 border-b">Description</th>
          <th className="px-4 py-2 border-b">Currency</th>
          <th className="px-4 py-2 border-b">Debit</th>
          <th className="px-4 py-2 border-b">Credit</th>
          <th className="px-4 py-2 border-b">Running Balance</th>
          <th className="px-4 py-2 border-b">Account Number</th>
        </tr>
      </thead>
      <tbody>
        {transactions.map((transaction) => (
          <TransactionRow key={transaction.hash} transaction={transaction} />
        ))}
      </tbody>
    </table>
  );
};

export default TransactionsTable;
