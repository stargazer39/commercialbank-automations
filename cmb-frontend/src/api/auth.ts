interface Credentials {
  username: string;
  password: string;
}

interface Tokens {
  accessToken: string;
}

interface TransactionQuery {
  accessToken: string;
  page: number;
  size: number;
}

interface Transaction {
  hash: string;
  createdAt: string; // or Date if you parse this value
  userId: string;
  transactionDate: string; // or Date if you parse this value
  description: string;
  currency: string;
  debit: number | null;
  credit: number | null;
  runningBalance: number;
  accountNumber: string;
}

interface TransactionList {
  transactionList: Transaction[];
}

const BASE_URL = "http://localhost:8080";

async function login({ username, password }: Credentials): Promise<Tokens> {
  const myHeaders = new Headers();
  myHeaders.append("Content-Type", "application/json");

  const raw = JSON.stringify({
    username,
    password,
  });

  const requestOptions = {
    method: "POST",
    headers: myHeaders,
    body: raw,
  };

  let response = await fetch(BASE_URL + "/user/token", requestOptions);

  if (!response.ok) {
    throw new Error("login response was not okay");
  }

  return response.json();
}

async function getTransactions({
  accessToken,
  page,
  size,
}: TransactionQuery): Promise<TransactionList> {
  const myHeaders = new Headers();
  myHeaders.append("Authorization", "Bearer " + accessToken);

  const requestOptions = {
    method: "GET",
    headers: myHeaders,
  };

  const response = await fetch(
    BASE_URL + `/transactions?page=${page}&size=${size}`,
    requestOptions
  );

  if (!response.ok) {
    throw new Error("getTransactions response was not okay");
  }

  return response.json();
}

export { login, getTransactions, BASE_URL };
export type { Credentials, Transaction, TransactionList };
