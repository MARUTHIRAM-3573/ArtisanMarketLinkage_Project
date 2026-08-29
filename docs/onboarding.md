# Onboarding

Zero-to-running guide for the Artisan Digital Commerce Platform monorepo.
Read `REPOSITORY_STRUCTURE.md` first if you haven't — it explains why this
repo looks the way it does (one Spring Boot deployable, not microservices;
React Native mobile, not a web app) and links back to the approved planning
documents under `docs/architecture/`.

## 1. Prerequisites

| Tool | Version | Used for |
|---|---|---|
| Java (Temurin) | 21 | Building/running the backend |
| Maven | 3.9+ | Backend build (or use the included `./mvnw` if you add one — none is committed here, install Maven directly) |
| Docker + Docker Compose | recent | Local stack (PostgreSQL + backend) |
| Node.js | 20 | Frontend tooling |
| npm | 10+ | Frontend package management |
| Expo Go app (Android) or an Android emulator | latest | Running the mobile app |
| PostgreSQL client (`psql`) | 16 | `infra/scripts/seed.sh`, manual DB inspection |

No Kubernetes, no RabbitMQ, no Redis, no Vault — none of those are part of
the approved MVP architecture (see `docs/architecture/16_ADR_PACK.md` and
`REPOSITORY_STRUCTURE.md`'s "What Was Deliberately Omitted" table).

## 2. Zero-to-running

```bash
git clone <this-repo>
cd artisan-platform

# One command: copies .env.example -> .env.local, builds and starts
# postgres + backend, waits for health checks, seeds demo data.
bash infra/scripts/bootstrap.sh
```

That's equivalent to running, by hand:

```bash
cp .env.example .env.local
make docker-up      # docker compose up -d --build
make health         # waits for postgres + backend health checks
make seed           # demo artisan user + one demo product
```

Then, in a second terminal, start the mobile app:

```bash
cd frontend
npm install
npx expo start
```

Press `a` to open it in an Android emulator, or scan the QR code with
Expo Go on a physical Android device. The app's API base URL
(`EXPO_PUBLIC_API_BASE_URL` in `frontend/.env`, defaulting to
`http://10.0.2.2:8080/api/v1`) assumes an Android emulator talking to the
backend on your host machine — on a physical device, point it at your
machine's LAN IP instead.

Sign in with the seeded demo account:

```
demo.artisan@artisanplatform.dev / Password123!
```

## 3. Verifying it worked

```bash
bash infra/scripts/smoke-test.sh
```

Runs an actuator health check, logs in as the demo user, and fetches the
product catalog with the resulting token. If this passes, the backend,
database, and auth flow are all wired correctly end to end.

## 4. Test commands

```bash
make test           # backend (mvn verify, incl. Testcontainers integration tests) + frontend (jest)
make lint           # backend (Checkstyle) + frontend (ESLint)
make format         # frontend (Prettier) — backend formatting is enforced, not auto-fixed

# Or individually:
mvn -f backend/pom.xml verify                 # backend unit + integration tests
npm test --prefix frontend                    # frontend unit tests
npm run type-check --prefix frontend          # frontend TypeScript check
```

## 5. Port table

| Service | Port | Notes |
|---|---|---|
| Backend (single deployable) | 8080 | All 11 domain modules served from one port — see `REPOSITORY_STRUCTURE.md` |
| PostgreSQL | 5432 | One database, one schema per domain module |
| Swagger UI | 8080 `/swagger-ui/index.html` | Interactive API docs (springdoc) |
| Actuator health | 8080 `/actuator/health` | Used by docker-compose health checks and `infra/scripts/health-check.sh` |
| Expo dev server | 19000-19002 | Metro bundler + Expo DevTools, only while `expo start` is running |

## 6. Reading order

New to this repo? Read in this order:

1. `docs/architecture/MVP.md` — what's actually in scope
2. `REPOSITORY_STRUCTURE.md` — why the repo is laid out this way
3. `docs/architecture/16_ADR_PACK.md` — the architecture decisions (modular monolith, no broker/cache/vault, React Native, etc.)
4. `docs/architecture/04_DATA_MODEL_AND_OWNERSHIP.md` — the 11 schemas, matched 1:1 by `backend/modules/*` and `database/migrations/*`
5. `docs/architecture/05_API_CONTRACTS.md` — matched by `frontend/src/api/endpoints.ts`
6. `docs/architecture/13_FRONTEND_DASHBOARD_PLAN.md` — matched by `frontend/src/navigation` and `frontend/src/screens`
7. `docs/architecture/17_CODING_BACKLOG.md` (a.k.a. `CODING_BACKLOG.md`) and `docs/architecture/CODEX_MEMORY.md` — what's left to build and why certain calls were made

## 7. Known gaps in this scaffold

This scaffold is a starting point, not a finished product. In particular:

- Only the primary aggregate-root entity per domain module is fully
  scaffolded (e.g. `catalog.products`, not every child table like
  `product_attributes`) — see `docs/architecture/17_CODING_BACKLOG.md` for
  the full entity list per module.
- The AI provider adapter (`backend/modules/ai/.../adapter/NoOpAiProviderAdapter.java`)
  and the payment gateway adapter
  (`backend/modules/payment/.../gateway/MockPaymentGatewayAdapter.java`) are
  explicitly non-production placeholders behind a real interface — swap in
  a real implementation of the adapter interface, nothing else needs to
  change (principle #14, provider-agnostic adapters).
- No deployment target is chosen yet, so `.github/workflows/cd.yml`'s
  backend deploy step is a placeholder that only builds and pushes the
  image to GHCR.
