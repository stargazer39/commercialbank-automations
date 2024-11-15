type Transaction = {
  hash: string;
  createdAt: string;
  userId: string;
  transactionDate: string;
  description: string;
  currency: string;
  debit: number | null;
  credit: number | null;
  runningBalance: number;
  accountNumber: string;
};

const TransactionRow = ({ transaction }: { transaction: Transaction }) => {
  return (
    <tr>
      <td className="px-4 py-2 border-b">{transaction.hash}</td>
      <td className="px-4 py-2 border-b">{transaction.createdAt}</td>
      <td className="px-4 py-2 border-b">{transaction.userId}</td>
      <td className="px-4 py-2 border-b">{transaction.transactionDate}</td>
      <td className="px-4 py-2 border-b">{transaction.description}</td>
      <td className="px-4 py-2 border-b">{transaction.currency}</td>
      <td className="px-4 py-2 border-b">{transaction.debit}</td>
      <td className="px-4 py-2 border-b">{transaction.credit}</td>
      <td className="px-4 py-2 border-b">{transaction.runningBalance}</td>
      <td className="px-4 py-2 border-b">{transaction.accountNumber}</td>
    </tr>
  );
};

const TransactionsTable = ({
  transactions,
}: {
  transactions: Transaction[];
}) => {
  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-100">
      <div className="w-full max-w-5xl p-8 bg-white rounded-lg shadow-md">
        <h2 className="text-2xl font-bold text-center text-gray-700 mb-6">
          Transactions
        </h2>
        <table className="w-full table-auto border-collapse">
          <thead>
            <tr className="bg-gray-200">
              <th className="px-4 py-2 border-b">Hash</th>
              <th className="px-4 py-2 border-b">Created At</th>
              <th className="px-4 py-2 border-b">User ID</th>
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
              <TransactionRow
                key={transaction.hash}
                transaction={transaction}
              />
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default TransactionsTable;