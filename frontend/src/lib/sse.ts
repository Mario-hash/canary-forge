export type CfEvent = {
  id?: string;
  type: "HIT" | "USED";
  tokenType: "URL" | "PIX" | "KEY";
  label: string;
  scenario: string;
  source?: { ipTrunc?: string; ua?: string; referrer?: string };
  severity: "LOW" | "MEDIUM" | "HIGH";
  createdAt: string;
};

type Options = {
  url?: string;
  onEvent?: (e: CfEvent) => void;
  onStatus?: (s: "connecting" | "open" | "closed" | "error") => void;
};

export function connectSSE({
  url = "/api/events/stream",
  onEvent,
  onStatus,
}: Options) {
  let es: EventSource | null = null;
  let retry = 0;
  const maxBackoff = 15000;

  const open = () => {
    onStatus?.("connecting");
    console.log("[SSE] opening", url); 
    es = new EventSource(url, { withCredentials: false });

    es.onmessage = (msg) => {
      console.log("[SSE:message]", msg.data);
      try {
        const data = JSON.parse(msg.data) as CfEvent;
        onEvent?.(data);
      } catch (e) {
        console.warn("[SSE] parse fail (message)", e);
      }
    };

    es.addEventListener("event", (msg) => {
      const dataStr = (msg as MessageEvent).data;
      console.log("[SSE:event]", dataStr); 
      try {
        const data = JSON.parse(dataStr) as CfEvent;
        onEvent?.(data);
      } catch (e) {
        console.warn("[SSE] parse fail (event)", e);
      }
    });

    es.addEventListener("heartbeat", () => {
    });

    es.onopen = () => {
      retry = 0;
      console.log("[SSE] open"); // 🔎
      onStatus?.("open");
    };

    es.onerror = (ev) => {
      console.warn("[SSE] error", ev); // 🔎
      onStatus?.("error");
      es?.close();
      es = null;
      retry = Math.min(retry + 1, 6);
      const backoff = Math.min(500 * 2 ** retry, maxBackoff);
      console.log("[SSE] retrying in", backoff, "ms"); // 🔎
      setTimeout(open, backoff);
    };
  };

  open();

  return () => {
    console.log("[SSE] closed by client"); // 🔎
    onStatus?.("closed");
    es?.close();
    es = null;
  };
}
