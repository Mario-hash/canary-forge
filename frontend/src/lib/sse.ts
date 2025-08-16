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
  url = "http://localhost:8080/api/events/stream",
  onEvent,
  onStatus,
}: Options) {
  let es: EventSource | null = null;
  let retry = 0;
  const maxBackoff = 15000;

  const open = () => {
    onStatus?.("connecting");
    es = new EventSource(url, { withCredentials: false });

    es.addEventListener("event", (msg) => {
      try {
        const data = JSON.parse((msg as MessageEvent).data) as CfEvent;
        onEvent?.(data);
      } catch {}
    });

    es.onmessage = (msg) => {
      try {
        const data = JSON.parse(msg.data) as CfEvent;
        onEvent?.(data);
      } catch {}
    };

    es.addEventListener("heartbeat", () => {});

    es.onopen = () => {
      retry = 0;
      onStatus?.("open");
    };

    es.onerror = () => {
      onStatus?.("error");
      es?.close();
      es = null;
      retry = Math.min(retry + 1, 6);
      const backoff = Math.min(500 * 2 ** retry, maxBackoff);
      setTimeout(open, backoff);
    };
  };

  open();

  return () => {
    onStatus?.("closed");
    es?.close();
    es = null;
  };
}
