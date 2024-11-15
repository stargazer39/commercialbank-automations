import { useEffect, useRef, useState } from "react";
import { BASE_URL } from "../api/auth";
import { useQuery } from "@tanstack/react-query";

interface WebSocketConfig {
  url: string;
  onMessage?: (message: any) => void;
  reconnect?: boolean;
  beforeReconnect?: () => any;
}

interface WebSocketState {
  status: "connecting" | "connected" | "error" | "closed";
}

const BASE_WEBSOCKET_URL = "ws://localhost:8080";

const useWebSocket = ({
  url,
  onMessage,
  reconnect = false,
  beforeReconnect,
}: WebSocketConfig) => {
  const [status, setStatus] = useState<WebSocketState>({
    status: "connecting",
  });

  const recon = useRef(true);

  useEffect(() => {
    const websocket = new WebSocket(url);
    websocket.onopen = () => {
      console.log("websocket open");
      setStatus({ status: "connected" });
    };

    websocket.onerror = (e) => {
      console.error(e);
      setStatus({ status: "error" });
      setTimeout(() => {
        recon.current = !recon.current;
      }, 1000);
    };

    websocket.onmessage = (m) => {
      console.log("websocket message");
      onMessage?.(m.data);
    };

    websocket.onclose = () => {
      console.log("websocket closed");
      setStatus({ status: "closed" });
      setTimeout(() => {
        recon.current = !recon.current;
      }, 1000);
    };

    return () => {
      console.log("websocket closed (render)");
      websocket.close();
    };
  }, [url, reconnect, recon.current]);

  return [status];
};

const useTransactionUpdates = (
  onUpdate: (update: any) => void,
  accessToken: string
) => {
  return useWebSocket({
    url: BASE_WEBSOCKET_URL + "/events/transactions?token=" + accessToken,
    reconnect: true,
    onMessage(message) {
      console.log("message", message);
      onUpdate(message);
    },
  });
};

export { useWebSocket, useTransactionUpdates };
