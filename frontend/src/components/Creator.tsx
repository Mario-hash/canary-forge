import { useState } from "react";
import { createToken, type CreateTokenReq, type TokenResponse } from "../lib/api";

export default function Creator() {
  const [type, setType] = useState<CreateTokenReq["type"]>("URL");
  const [label, setLabel] = useState("cv");
  const [scenario, setScenario] = useState("resume");
  const [ttlSec, setTtlSec] = useState(3600);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [resp, setResp] = useState<TokenResponse | null>(null);

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setResp(null);
    try {
      const r = await createToken({ type, label, scenario, ttlSec });
      setResp(r);
    } catch (err: any) {
      setError(err?.message ?? "Unknown error");
    } finally {
      setLoading(false);
    }
  }

  function copy(text?: string | null) {
    if (!text) return;
    navigator.clipboard.writeText(text).catch(() => {});
  }

  return (
    <div className="max-w-2xl mx-auto p-4 space-y-4">
      <h2 className="text-xl font-semibold">Crear cebo</h2>

      <form onSubmit={onSubmit} className="space-y-3 bg-zinc-900/40 p-4 rounded-2xl border border-zinc-800">
        <div className="grid grid-cols-2 gap-3">
          <label className="flex flex-col gap-1">
            <span className="text-sm text-zinc-300">Tipo</span>
            <select
              className="bg-zinc-900 border border-zinc-700 rounded-xl px-3 py-2"
              value={type}
              onChange={(e) => setType(e.target.value as any)}
            >
              <option value="URL">URL</option>
              <option value="PIX">PIX (1×1)</option>
            </select>
          </label>

          <label className="flex flex-col gap-1">
            <span className="text-sm text-zinc-300">TTL (segundos)</span>
            <input
              type="number"
              min={60}
              max={60*60*24*30}
              className="bg-zinc-900 border border-zinc-700 rounded-xl px-3 py-2"
              value={ttlSec}
              onChange={(e) => setTtlSec(parseInt(e.target.value || "0", 10))}
            />
          </label>

          <label className="flex flex-col gap-1">
            <span className="text-sm text-zinc-300">Label</span>
            <input
              className="bg-zinc-900 border border-zinc-700 rounded-xl px-3 py-2"
              value={label}
              onChange={(e) => setLabel(e.target.value)}
            />
          </label>

          <label className="flex flex-col gap-1">
            <span className="text-sm text-zinc-300">Scenario</span>
            <input
              className="bg-zinc-900 border border-zinc-700 rounded-xl px-3 py-2"
              value={scenario}
              onChange={(e) => setScenario(e.target.value)}
            />
          </label>
        </div>

        <div className="flex gap-2">
          <button
            disabled={loading}
            className="bg-emerald-600 hover:bg-emerald-500 disabled:opacity-60 rounded-xl px-4 py-2 font-medium"
          >
            {loading ? "Creando..." : "Crear"}
          </button>
          {error && <span className="text-red-400 text-sm">{error}</span>}
        </div>
      </form>

      {resp && (
        <div className="space-y-3 bg-zinc-900/40 p-4 rounded-2xl border border-zinc-800">
          <h3 className="font-medium">Resultado</h3>

          {resp.url && (
            <div className="flex items-center gap-2">
              <code className="bg-black/30 border border-zinc-800 rounded-lg px-2 py-1 text-sm overflow-x-auto">
                {resp.url}
              </code>
              <button
                className="text-xs border border-zinc-700 rounded-lg px-2 py-1 hover:bg-zinc-800"
                onClick={() => copy(resp.url!)}
              >
                Copiar URL
              </button>
            </div>
          )}

          {resp.html && (
            <div className="flex items-center gap-2">
              <code className="bg-black/30 border border-zinc-800 rounded-lg px-2 py-1 text-sm overflow-x-auto">
                {resp.html}
              </code>
              <button
                className="text-xs border border-zinc-700 rounded-lg px-2 py-1 hover:bg-zinc-800"
                onClick={() => copy(resp.html!)}
              >
                Copiar &lt;img/&gt;
              </button>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
