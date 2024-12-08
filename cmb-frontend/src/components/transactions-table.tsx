import { useState } from "react";
import { Transaction } from "../api/auth";
import { capitalizeFirstLetter } from "../helper/text";

const TransactionRow = ({ transaction }: { transaction: Transaction }) => {
  const [defaulTag, setDefaultTag] = useState(
    capitalizeFirstLetter(transaction.defaultTag || "No tag")
  );
  const onDefocus = () => {
    console.log(defaulTag)
  };

  const rowItemclass = "px-4 py-3 border-b text-center";
  return (
    <tr
      className={`${
        transaction.credit ? "bg-green-50" : "bg-red-50"
      } hover:bg-gray-100 transition-colors duration-200`}
    >
      <td
        className={`border-none ${
          transaction.account.accountType == "CreditCard"
            ? "bg-fuchsia-600 w-1"
            : "w-1"
        }`}
      ></td>
      <td className={rowItemclass}>{transaction.transactionDate}</td>
      <td className={rowItemclass}>{transaction.description}</td>
      <td className={rowItemclass}>{transaction.currency}</td>
      <td className={rowItemclass}>
        {transaction.debit ? transaction.debit : transaction.credit}
      </td>
      {/* <td className={rowItemclass}>{transaction.runningBalance}</td> */}
      <td className={rowItemclass}>{transaction.account.accountNumber}</td>
      <td className={rowItemclass}>
        <input
          type="text"
          value={defaulTag}
          onChange={(e) => setDefaultTag(e.target.value)}
          onBlur={onDefocus}
          className="bg-transparent focus:bg-white"
        />
      </td>
    </tr>
  );
};

const TransactionsTable = ({
  transactions,
}: {
  transactions: Transaction[];
}) => {
  return (
    <div className="overflow-x-auto shadow-md rounded-lg">
      <table className="w-full table-auto border-collapse text-sm border border-gray-300">
        <thead className="bg-gray-100 text-gray-700">
          <tr>
            <th className="w-1 border-b"></th>
            <th className="px-4 py-3 border-b text-left">Transaction Date</th>
            <th className="px-4 py-3 border-b text-left">Description</th>
            <th className="px-4 py-3 border-b text-left">Currency</th>
            <th className="px-4 py-3 border-b text-right">Amount</th>
            {/* <th className="px-4 py-3 border-b text-right">Running Balance</th> */}
            <th className="px-4 py-3 border-b text-left">Account Number</th>
            <th className="px-4 py-3 border-b text-left">Tag</th>
          </tr>
        </thead>
        <tbody>
          {transactions.map((transaction) => (
            <TransactionRow key={transaction.hash} transaction={transaction} />
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default TransactionsTable;
