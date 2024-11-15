interface Credentials {
  username: string;
  password: string;
}
const BASE_URL = "http://localhost:8080";

function login({ username, password }: Credentials) {
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

  return fetch(BASE_URL + "/user/token", requestOptions);
}

export { login };
export type { Credentials };
