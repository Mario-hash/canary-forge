import { useEffect, useMemo, useState } from "react";
import { connectSSE } from "../lib/sse"; 
import type { CfEvent } from "../lib/sse"; 

function badgeColor(sev: CfEvent["severity"]) {
  switch (sev) {
    case "HIGH": return "bg-red-600 text-white";
    case "MEDIUM": return "bg-amber-500 text-black";
    default: return "bg-emerald-500 text-black";
  }
}
function fmtTime(iso: string) {
  const d = new Date(iso);
  return d.toLocaleTimeString();
}

export default function LiveFeed() {
  const [events, setEvents] = useState<CfEvent[]>([]);
  const [status, setStatus] = useState<"connecting"|"open"|"closed"|"error">("connecting");
  const [q, setQ] = useState("");
  const [sev, setSev] = useState<"ALL"|"LOW"|"MEDIUM"|"HIGH">("ALL");
  const [type, setType] = useState<"ALL"|"URL"|"PIX"|"KEY">("ALL");

  useEffect(() => {
    const dispose = connectSSE({
      onEvent: (e) => {
        setEvents(prev => [e, ...prev].slice(0, 200)); 
      },
      onStatus: setStatus
    });
    return dispose;
  }, []);

  const filtered = useMemo(() => {
    return events.filter(e => {
      const passSev = sev === "ALL" || e.severity === sev;
      const passType = type === "ALL" || e.tokenType === type;
      const text = `${e.label} ${e.scenario} ${e.source?.ua ?? ""} ${e.source?.referrer ?? ""} ${e.source?.ipTrunc ?? ""}`.toLowerCase();
      const passQ = !q || text.includes(q.toLowerCase());
      return passSev && passType && passQ;
    });
  }, [events, sev, type, q]);

  return (
    <div className="min-h-screen bg-white dark:bg-neutral-900 text-neutral-900 dark:text-neutral-100">
      <div className="max-w-6xl mx-auto p-6 space-y-4">
        <header className="flex items-center justify-between">
          <h1 className="text-2xl font-bold">Canary Forge — Live Feed</h1>
          <span className={`text-xs px-2 py-1 rounded ${
              status === "open" ? "bg-emerald-200 text-emerald-900" :
              status === "connecting" ? "bg-amber-200 text-amber-900" :
              "bg-rose-200 text-rose-900"}`}>
            {status}
          </span>
        </header>

        <div className="grid grid-cols-1 md:grid-cols-4 gap-3">
          <input
            className="md:col-span-2 border rounded px-3 py-2"
            placeholder="Buscar (label, scenario, UA, referrer, IP…)"
            value={q} onChange={(e)=>setQ(e.target.value)}
          />
          <select className="border rounded px-3 py-2" value={sev} onChange={(e)=>setSev(e.target.value as any)}>
            <option value="ALL">Severidad: todas</option>
            <option value="LOW">LOW</option>
            <option value="MEDIUM">MEDIUM</option>
            <option value="HIGH">HIGH</option>
          </select>
          <select className="border rounded px-3 py-2" value={type} onChange={(e)=>setType(e.target.value as any)}>
            <option value="ALL">Tipo: todos</option>
            <option value="URL">URL</option>
            <option value="PIX">PIX</option>
            <option value="KEY">KEY</option>
          </select>
        </div>

        <div className="overflow-x-auto rounded-xl border">
          <table className="min-w-full text-sm">
            <thead className="bg-gray-50 dark:bg-gray-800">
              <tr>
                <th className="text-left px-3 py-2">Hora</th>
                <th className="text-left px-3 py-2">Tipo</th>
                <th className="text-left px-3 py-2">Label</th>
                <th className="text-left px-3 py-2">Scenario</th>
                <th className="text-left px-3 py-2">IP</th>
                <th className="text-left px-3 py-2">UA</th>
                <th className="text-left px-3 py-2">Referrer</th>
                <th className="text-left px-3 py-2">Sev</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map((e, i) => (
                <tr key={`${e.createdAt}-${i}`} className="border-t">
                  <td className="px-3 py-2 whitespace-nowrap">{fmtTime(e.createdAt)}</td>
                  <td className="px-3 py-2">{e.type} / {e.tokenType}</td>
                  <td className="px-3 py-2">{e.label}</td>
                  <td className="px-3 py-2">{e.scenario}</td>
                  <td className="px-3 py-2">{e.source?.ipTrunc ?? "-"}</td>
                  <td className="px-3 py-2 max-w-[280px] truncate" title={e.source?.ua}>{e.source?.ua ?? "-"}</td>
                  <td className="px-3 py-2 max-w-[200px] truncate" title={e.source?.referrer}>{e.source?.referrer ?? "-"}</td>
                  <td className="px-3 py-2">
                    <span className={`text-xs px-2 py-1 rounded ${badgeColor(e.severity)}`}>{e.severity}</span>
                  </td>
                </tr>
              ))}
              {filtered.length === 0 && (
                <tr>
                  <td colSpan={8} className="px-3 py-10 text-center text-gray-500">
                    No hay eventos todavía. Crea un token o pixel y ábrelo para verlos aparecer aquí en tiempo real.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

      </div>
    </div>
  );
}
