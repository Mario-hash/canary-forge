export type CreateTokenReq = {
  type: "URL" | "PIX";
  label: string;
  scenario: string;
  ttlSec: number;
};

export type TokenResponse = {
  url: string | null;
  html: string | null;
  key: string | null;
  hintEndpoint: string | null;
};

export async function createToken(req: CreateTokenReq): Promise<TokenResponse> {
  const res = await fetch("/api/tokens", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
    },
    body: JSON.stringify({
      ...req,
      ttlSec: Number(req.ttlSec),
    }),
  });

  if (!res.ok) {
    const txt = await res.text();
    throw new Error(`HTTP ${res.status} ${res.statusText} — ${txt}`);
  }
  return res.json();
}
