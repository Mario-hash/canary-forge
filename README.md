# 🐤 Canary Forge

Proyecto de **monitorización de clics y píxeles de seguimiento** con backend en **Spring Boot + MongoDB** y frontend en **React + Tailwind**.

---

## 🚀 Quickstart

Arráncalo en local en **3 pasos**:

```bash
# 1. Levantar MongoDB
docker compose -f infra/docker-compose.yml up -d

# 2. Backend
(cd backend && mvn spring-boot:run)

# 3. Frontend
(cd frontend && npm i && npm run dev && start http://localhost:5173)
```

El frontend se sirve en:
👉 [http://localhost:5173](http://localhost:5173)

---

## 🔑 Variables de entorno

El backend necesita `CF_SECRET` (clave para firmar tokens) y `MONGO_URI` (URL de MongoDB).

### 🟢 Opción 1: Usar VS Code (recomendado ✅)

Ya están configuradas en `backend/.vscode/launch.json`:

```json
"env": {
  "CF_SECRET": "xxxx...base64...",
  "MONGO_URI": "mongodb://localhost:27017/canaryforge"
}
```

👉 Basta con pulsar **F5** en VS Code para arrancar el backend con todo configurado.

---

### 🟠 Opción 2: Usar terminal con Maven

Si prefieres ejecutar `mvn spring-boot:run` en consola, exporta las variables antes:

#### 🪟 Windows PowerShell

```powershell
$env:CF_SECRET = "<pega_un_base64_de_32_bytes>"
$env:MONGO_URI = "mongodb://localhost:27017/canaryforge"
mvn spring-boot:run
```

#### 🐧 macOS / Linux

```bash
export CF_SECRET="$(openssl rand -base64 32)"
export MONGO_URI="mongodb://localhost:27017/canaryforge"
mvn spring-boot:run
```

---

## 📡 Probar la app

### 1️⃣ Crear un token de cebo

**Request:**

```http
POST http://localhost:8080/api/tokens
Content-Type: application/json
```

**Body:**

```json
{
  "type": "URL",
  "label": "cv",
  "scenario": "resume",
  "ttlSec": 3600
}
```

**Respuesta:**

```json
{
  "url": "/c/eyJzYyI6..."
}
```

---

### 2️⃣ Visitar la URL de cebo o pixel

- **Clic:**
  `GET http://localhost:8080/c/{sig}` → registra un clic (**204 No Content**)

- **Pixel:**
  `GET http://localhost:8080/p/{sig}` → devuelve un pixel transparente (**200 OK**, `image/png`)

---

### 3️⃣ Ver eventos en tiempo real

```http
GET http://localhost:8080/api/events/stream
```

Ábrelo en navegador o Postman → **Server-Sent Events (SSE)**.

👉 Cada clic/pixel aparece como un **nuevo evento en vivo**.
