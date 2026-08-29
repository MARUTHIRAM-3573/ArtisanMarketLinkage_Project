# 15 — Repository Structure

## 1. Monorepo vs. Polyrepo Decision

**Monorepo.** Source §35 prescribes exactly one repository (`artisan-platform/`) containing `backend/`, `frontend/`, `database/`, `docs/`, and `storage/` as siblings — this is an explicit, literal structure from the source document, not an inference. A polyrepo split would also contradict the modular-monolith architecture decision (`02_ARCHITECTURE_OVERVIEW.md` §3): one deployable backend and one mobile app are naturally versioned and reviewed together during a hackathon.

## 2. Full Annotated Directory Tree

```text
artisan-platform/
├── backend/                        # Spring Boot application (single deployable, modular monolith)
│   ├── pom.xml                     # Maven build file (explicit, source §35)
│   └── src/
│       ├── main/
│       │   ├── java/.../
│       │   │   ├── auth/           # Identity module (source names this package "auth"; schema is "identity" — see open question below)
│       │   │   ├── seller/         # Seller module
│       │   │   ├── catalog/        # Catalog module
│       │   │   ├── media/          # Media module
│       │   │   ├── inventory/      # Inventory module
│       │   │   ├── ai/             # AI orchestration module
│       │   │   ├── pricing/        # Pricing module
│       │   │   ├── market/         # Market module
│       │   │   ├── b2b/            # B2B module
│       │   │   ├── commerce/       # Commerce module
│       │   │   ├── payment/        # Payment module
│       │   │   ├── fulfillment/    # PROPOSED module, not in source §29's list — pending approval
│       │   │   ├── experience/     # PROPOSED module, not in source §29's list — pending approval
│       │   │   └── common/         # Shared DTOs, error handling, audit fields, MediaStorageService interface (reasoned, see 03_SERVICE_BOUNDARIES.md §5.2)
│       │   └── resources/
│       │       ├── application.yml
│       │       └── application-{local,dev,staging,prod}.yml
│       └── test/                   # Unit + integration tests, mirrors main/java structure
│
├── frontend/                        # React Native + TypeScript + Expo (source §35, §5)
│   ├── app.json / app.config.ts     # Expo config
│   ├── package.json
│   └── src/
│       ├── screens/                # Per source §27's responsibility list — see 13_FRONTEND_DASHBOARD_PLAN.md §3
│       ├── components/
│       ├── services/                # API client modules, one per backend module
│       └── navigation/
│
├── database/
│   └── migrations/                  # Flyway-versioned SQL (source §35, §36)
│       ├── V001__create_identity.sql
│       ├── ...
│       └── V0NN__<description>.sql
│
├── docs/                             # (source §35)
│   ├── PROJECT_ARCHITECTURE.md       # This source-of-truth document, kept current per source §45
│   ├── DEVELOPMENT_STATUS.md         # (source §35)
│   └── openapi/                      # OpenAPI 3.0 spec tree, see 05_API_CONTRACTS.md §6
│
├── storage/                          # (source §35)
│   └── README.md                     # Documents the local uploads/ layout (source §12); actual runtime uploads/ is gitignored
│
├── .github/
│   └── workflows/
│       └── ci.yml                    # See 10_CI_CD_AND_ENVIRONMENTS.md §A.6
│
├── docker-compose.yml                 # See 10_CI_CD_AND_ENVIRONMENTS.md §B.5
├── CODEOWNERS                         # See 03_SERVICE_BOUNDARIES.md §6
├── README.md                          # (source §35)
└── .gitignore                         # Must exclude uploads/, .env, secrets (source §32.8, §35)
```

> ⚠️ Open Question: The `experience` module has no package name precedent anywhere in source (§29's recommended structure omits it entirely, unlike `fulfillment` which is at least named there) — `experience` above is this document's own reasoned naming, not a literal source name — blocks: 15_REPOSITORY_STRUCTURE.md, 03_SERVICE_BOUNDARIES.md

## 3. Naming Conventions

| Artifact | Convention | Source basis |
|---|---|---|
| Files/directories | `kebab-case` for docs, `lowercase` package names for Java (`auth`, `catalog`, etc.) | Source §29 literal package names |
| Branches | `feature/<name>`, `fix/<name>` | Explicit, source §34 |
| Commits | Logical, grouped by feature (not per-class), imperative mood (e.g., "Implement <feature>") | Explicit, source §34: "Do not commit/push every Java class separately" |
| Pull Requests | One PR per feature branch, merged into `main` | Explicit, source §34 |
| Migration files | `V<seq>__<snake_case_description>.sql` | Explicit, source §36 |
| Database tables | `<schema>.<snake_case_plural_noun>` | Explicit throughout source §8-24 |
| API paths | `/api/v1/<kebab-or-plural-noun>` | Explicit, source §30 |

## 4. Shared Package and Library Strategy

Reused from `03_SERVICE_BOUNDARIES.md` §5.2: a `common/` package inside `backend/src/main/java/...` holds shared DTO conventions, error handling, audit-column base entity, and the `MediaStorageService` interface. No separate published/versioned shared library (e.g., a private Maven artifact) is warranted at this scale — it lives as an ordinary package inside the single backend module, consistent with the monorepo/modular-monolith decision.

## 5. CODEOWNERS File Structure

See `03_SERVICE_BOUNDARIES.md` §6 for the full listing; it lives at the repository root as `CODEOWNERS` per GitHub convention.

## 6. New Engineer Onboarding Guide

### 6.1 Prerequisites: Tools, Accounts, Access

- Visual Studio Code (primary IDE, source §28).
- Java 21 (or whichever LTS the team pins), Maven, Node.js, npm, Expo CLI.
- Android SDK + Android Emulator, or a physical Android device with USB debugging (Android Studio optional, source §28).
- Git, and a GitHub account with repository access.
- Docker (for local PostgreSQL via `docker-compose.yml`).

### 6.2 Step-by-Step Local Setup from Zero to Running

1. Clone the repository: `git clone <repo-url> && cd artisan-platform`.
2. Follow `10_CI_CD_AND_ENVIRONMENTS.md` §B.5 exactly: bring up PostgreSQL via Docker Compose, run Flyway migrations, start the backend, start the Expo dev server.
3. Verify: `curl http://localhost:8080/api/v1/products` returns an empty/seeded list; the Expo app loads on an emulator/device pointed at the local backend.

### 6.3 How to Run the Test Suite

- Backend: `mvn -f backend/pom.xml verify` (runs unit + Testcontainers integration tests, per `09_TESTING_STRATEGY.md`).
- Frontend: `npm test --prefix frontend` (Jest).

### 6.4 How to Make Your First Contribution

1. `git checkout main && git pull origin main`
2. `git checkout -b feature/<your-feature-name>`
3. Implement, following the module boundaries in `03_SERVICE_BOUNDARIES.md` — never write directly into another module's schema/tables.
4. Run the test suite locally (§6.3).
5. `git add . && git commit -m "Implement <feature>"` (one logical commit per meaningful change, source §34).
6. `git push -u origin feature/<your-feature-name>`, open a Pull Request into `main`, request review from the relevant CODEOWNERS.

### 6.5 Architecture Orientation: What to Read and in What Order

1. `01_PRODUCT_SCOPE.md` — understand the problem and MVP boundary first.
2. `02_ARCHITECTURE_OVERVIEW.md` — understand the modular monolith decision and hard constraints before touching any code.
3. `03_SERVICE_BOUNDARIES.md` — find the module you'll be working in and its dependencies.
4. `04_DATA_MODEL_AND_OWNERSHIP.md` — understand the schema you'll be reading/writing.
5. `05_API_CONTRACTS.md` — understand the contract you're implementing or consuming.
6. `06_COMMUNICATION_WORKFLOWS.md` — understand how your module fits into the broader sequence for the user journey you're touching.
7. The remaining documents (07–17) as needed for the specific concern at hand (queueing/caching, security, testing, CI/CD, observability, resilience, frontend, roadmap, ADRs, backlog).

This ordering mirrors the source document's own instruction (§41): "Read this document. Identify the requested task. Identify which existing domain(s) are affected... Follow the existing architecture."
