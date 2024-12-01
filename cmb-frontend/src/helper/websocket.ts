import { useEffect, useRef, useState } from "react";
import Configuration from "../config";
import { Client } from "@stomp/stompjs";

interface WebSocketConfig {
  url: string;
  onMessage?: (message: any) => void;
  reconnect?: boolean;
  beforeReconnect?: () => any;
  topic: string;
  accessToken?: string;
}

interface WebSocketState {
  status: "connecting" | "connected" | "error" | "closed";
}

const useWebSocket = ({
  url,
  onMessage,
  reconnect = false,
  accessToken,
  topic,
}: WebSocketConfig) => {
  const [status, setStatus] = useState<WebSocketState>({
    status: "connecting",
  });

  const recon = useRef(true);

  useEffect(() => {
    const client = new Client({
      brokerURL: url,
      onConnect: () => {
        setStatus({ status: "connected" });
        client.subscribe(topic, (message) => {
          console.log(`Received: ${message.body}`);
          onMessage?.(message.body);
        });
      },
      connectHeaders: {
        passcode: accessToken ?? "",
      },
    });

    client.activate();

    return () => {
      client.deactivate();
    };
  }, [url, reconnect, recon.current]);

  return [status];
};

const useTransactionUpdates = (
  onUpdate: (update: any) => void,
  accessToken: string
) => {
  return useWebSocket({
    url: Configuration.getWebsocketEndpoint("/events"),
    topic: Configuration.getTransactionsTopic(),
    reconnect: true,
    accessToken,
    onMessage(message) {
      console.log("message", message);
      onUpdate(message);
    },
  });
};

export { useWebSocket, useTransactionUpdates };
