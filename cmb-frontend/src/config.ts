let apiHostname = "cmbautomation.local.op1.dehemi.com";
let apiEndpoint = `https://${apiHostname}`;
let websocketEndpoint = `wss://${apiHostname}`;
let transactionsTopic = "/topic/transactions";

let Configuration = {
  getAPIEndpoint(subpath: string): string {
    return apiEndpoint + subpath;
  },
  getWebsocketEndpoint(subpath: string): string {
    return websocketEndpoint + subpath;
  },
  getTransactionsTopic() {
    return transactionsTopic;
  },
};

export default Configuration;
