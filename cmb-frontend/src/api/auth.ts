import Configuration from "../config";
import { urlQueryParamsBuilder } from "../helper/url";

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
  tag?: string;
  start?: string;
  end?: string;
  accountNumber?: string[];
  type?: string;
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
  account: {
    accountNumber: string;
    accountType: "CreditCard" | "Savings"
  },
  defaultTag: string;
}

interface TransactionList {
  transactionList: Transaction[];
}

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
  console.log(Configuration.getAPIEndpoint("/user/token"));
  let response = await fetch(
    Configuration.getAPIEndpoint("/user/token"),
    requestOptions
  );

  if (!response.ok) {
    throw new Error("login response was not okay");
  }

  return response.json();
}

async function getTransactions({
  accessToken,
  page,
  size,
  tag,
  start,
  end,
  type
}: TransactionQuery): Promise<TransactionList> {
  const myHeaders = new Headers();
  myHeaders.append("Authorization", "Bearer " + accessToken);

  const requestOptions = {
    method: "GET",
    headers: myHeaders,
  };

  const response = await fetch(
    urlQueryParamsBuilder(Configuration.getAPIEndpoint(`/transactions`), {
      page,
      size,
      tag,
      start,
      end,
      type
    }),
    requestOptions
  );

  if (!response.ok) {
    throw new Error("getTransactions response was not okay");
  }

  return response.json();
}

async function getSummery({
  accessToken,
  start,
  end,
}: TransactionQuery): Promise<any> {
  const myHeaders = new Headers();
  myHeaders.append("Authorization", "Bearer " + accessToken);

  const requestOptions = {
    method: "GET",
    headers: myHeaders,
  };

  const response = await fetch(
    urlQueryParamsBuilder(Configuration.getAPIEndpoint(`/transactions/debit/by-default-tag`), {
      start,
      end,
    }),
    requestOptions
  );

  if (!response.ok) {
    throw new Error("getTransactions response was not okay");
  }

  return response.json();
}

export { login, getTransactions, getSummery };
export type { Credentials, Transaction, TransactionList };
