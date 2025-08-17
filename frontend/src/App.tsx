import { useState } from "react";
import LiveFeed from "./components/LiveFeed";
import Creator from "./components/Creator";

export default function App() {
  const [tab, setTab] = useState<"feed" | "creator">("feed");

  return (
    <div className="min-h-screen bg-zinc-950 text-zinc-100">
      <header className="border-b border-zinc-800 px-6 py-4 flex items-center gap-4">
        <h1 className="text-lg font-semibold">Canary Forge</h1>
        <nav className="ml-auto flex gap-2">
          <button
            className={`px-3 py-1.5 rounded-lg border ${tab==="feed" ? "border-emerald-600 bg-emerald-600/20" : "border-zinc-700 hover:bg-zinc-900"}`}
            onClick={() => setTab("feed")}
          >
            Live Feed
          </button>
          <button
            className={`px-3 py-1.5 rounded-lg border ${tab==="creator" ? "border-emerald-600 bg-emerald-600/20" : "border-zinc-700 hover:bg-zinc-900"}`}
            onClick={() => setTab("creator")}
          >
            Creator
          </button>
        </nav>
      </header>

      <main className="p-6">
        {tab === "feed" ? <LiveFeed/> : <Creator/>}
      </main>
    </div>
  );
}
