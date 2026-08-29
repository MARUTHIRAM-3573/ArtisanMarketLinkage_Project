# Product Completion Board — Artisan Digital Commerce Platform

This board is the single, exhaustive task list and progress tracker for taking the Artisan Digital Commerce Platform from zero to production. Every task below is traceable to one or more of the 17 planning documents, `MVP.md`, and `17_CODING_BACKLOG.md` under `docs/architecture/`. Sections are ordered by implementation sequence: infrastructure and CI/CD first, then the Phase 1–4 domain modules in dependency order (per the dependency graph in `03_SERVICE_BOUNDARIES.md` and the WBS in `14_IMPLEMENTATION_ROADMAP.md`), then the two PROPOSED domains (Fulfillment, Experience — pending team approval, per `01_PRODUCT_SCOPE.md` §23–24), then the frontend, then cross-cutting hardening, testing, and release readiness.

## 📊 Progress Dashboard

| Metric | Value |
|---|---|
| Total Tasks | 154 |
| ✅ Completed | 0 |
| 🔄 In Progress | 0 |
| ⬜ Not Started | 154 |
| Overall Progress | 0% |
| 🔴 Critical Path Remaining | 41 |
| Last Updated | — |

> ### How to Use This Board
> - This file is the single source of truth for project delivery progress
> - When starting a task: change `[ ]` → `[~]`
> - When completing a task: change `[~]` → `[x]`
> - After every update: recalculate section totals, progress bars, and the dashboard
> - Never mark a task `[x]` unless every part of its description is fully done
> - Update **Last Updated** on every change
> - This board is designed to be updated by an AI coding agent after each task

### 1. Infrastructure & Repository Foundation — ⬜ 0 / 8 complete [░░░░░░░░░░] 0%

#### 1.1 Monorepo & Backend Skeleton — ⬜ 0 / 5 complete

- [ ] `INFRA-001` **🔴 Scaffold the Maven multi-module backend reactor**
  `[type: infra]` `[size: L]`
  ⛔ BLOCKS: AUTH-001
  > Create the root `backend/pom.xml` reactor with one Maven module per approved domain (identity/auth, seller, catalog, media, inventory, ai, pricing, market, b2b, commerce, payment) plus `common`, `test-support`, and the `app` module that assembles the single Spring Boot deployable, per `docs/architecture/15_REPOSITORY_STRUCTURE.md` §2 and the 11-approved-module catalogue in `03_SERVICE_BOUNDARIES.md`.

- [ ] `INFRA-002` **Build the shared `common` library module**
  `[type: infra]` `[size: M]`
  > Implement `backend/common` (the `ApiResponse` envelope, global exception handling, UUID/audit base entities, `GlobalConstants` including `Roles.ARTISAN` etc.) as the only code every domain module may depend on, per the shared-nothing policy in `03_SERVICE_BOUNDARIES.md` §5 — no shared entities, only cross-cutting utilities.

- [ ] `INFRA-003` **Build the shared `test-support` library module**
  `[type: infra]` `[size: S]`
  > Implement `backend/test-support` with Testcontainers-PostgreSQL base test classes and shared fixtures reused by every module's integration test suite, matching the test pyramid described in `09_TESTING_STRATEGY.md`.

- [ ] `INFRA-004` **Wire the `app` module's Spring Boot entry point and health endpoint**
  `[type: feature]` `[size: M]`
  > Implement the single deployable's `Application` main class in `backend/app`, wiring Spring Boot Actuator with a custom `/actuator/health` check that verifies the database connection, matching the port table in `docs/onboarding.md` §5 and the health-check contract used by `infra/scripts/smoke-test.sh`.

- [ ] `INFRA-005` **Author `CODEOWNERS`**
  `[type: infra]` `[size: S]`
  > Create a `CODEOWNERS` file mapping every backend module directory and the `frontend/` directory to its owning team, per `03_SERVICE_BOUNDARIES.md` §6 and `15_REPOSITORY_STRUCTURE.md` §5.

#### 1.2 Local Environment & Database Runtime — ⬜ 0 / 3 complete

- [ ] `INFRA-006` **🔴 Author `docker-compose.yml` (PostgreSQL + backend only)**
  `[type: infra]` `[size: M]`
  ⛔ BLOCKS: AUTH-001
  > Create a docker-compose stack with exactly two services — `postgres` (database `artisan_marketplace`) and `backend` (built from the `app` module's Dockerfile) — with health checks, per the example in `10_CI_CD_AND_ENVIRONMENTS.md` Part B. Do not add RabbitMQ, Redis, or Vault services: MVP explicitly excludes them per ADR-4/ADR-10 in `16_ADR_PACK.md`.

- [ ] `INFRA-007` **Create `.env.example` and env-var management**
  `[type: config]` `[size: S]`
  > Enumerate every required environment variable (DB credentials, JWT secret, AI provider config, payment gateway config) in `.env.example`, matching the env-vars-only MVP secrets decision in `08_SECURITY_AND_VAULT.md` Part B, with `.env.local` git-ignored.

- [ ] `INFRA-008` **Write bootstrap and onboarding scripts**
  `[type: infra]` `[size: S]`
  > Write `infra/scripts/bootstrap.sh` (copy env, `docker compose up`, wait for health, seed demo data) and `docs/onboarding.md`'s zero-to-running guide, per `15_REPOSITORY_STRUCTURE.md` §6, so a new developer or AI agent can get the stack running with one command.

### 2. CI/CD Pipeline & Environments — ⬜ 0 / 10 complete [░░░░░░░░░░] 0%

#### 2.1 Pipeline Definition — ⬜ 0 / 4 complete

- [ ] `CICD-001` **Author `.github/workflows/ci.yml`**
  `[type: infra]` `[size: M]`
  > Implement the pipeline stage table from `10_CI_CD_AND_ENVIRONMENTS.md` Part A (lint → build → unit test → integration test → security scan) for both the backend (Maven) and the frontend (npm), using the YAML skeleton given in that document.

- [ ] `CICD-002` **Author `.github/workflows/cd.yml`**
  `[type: infra]` `[size: M]`
  > Build the `app` module's Docker image and push it to GHCR on merge to main. Leave the deploy step as an explicit placeholder pending the deployment-provider decision (tracked by DOC-006) — do not guess a target.

- [ ] `CICD-003` **Configure branch protection and document rollback strategy**
  `[type: config]` `[size: S]`
  > Enable branch protection requiring a passing CI run before merge, and document the rollback strategy (revert the merge commit, redeploy the previous GHCR image tag) from `10_CI_CD_AND_ENVIRONMENTS.md` Part A.

- [ ] `CICD-004` **Write the multi-stage backend Dockerfile**
  `[type: infra]` `[size: S]`
  > Create `backend/app/Dockerfile` using a multi-stage Maven build producing a minimal runtime image, per the Docker image strategy in `10_CI_CD_AND_ENVIRONMENTS.md` Part A.

#### 2.2 Environments & Promotion — ⬜ 0 / 6 complete

- [ ] `CICD-005` **Define the environment catalogue (Local/Dev/Staging/Production)**
  `[type: config]` `[size: M]`
  > Create the per-environment configuration files/variables for Local, Dev, Staging, and Production per the environment catalogue table in `10_CI_CD_AND_ENVIRONMENTS.md` Part B.

- [ ] `CICD-006` **Implement the Dev→Staging→Production promotion flow**
  `[type: infra]` `[size: M]`
  > Wire the promotion-flow diagram from Part B into the CD pipeline (CICD-002) so a build artifact is promoted between environments rather than rebuilt per environment.

- [ ] `CICD-007` **Wire Testcontainers-backed integration tests into CI**
  `[type: test]` `[size: S]`
  > Configure the CI stage so `mvn -f backend/pom.xml verify` runs against an ephemeral PostgreSQL via Testcontainers, exactly as it runs locally, per INFRA-003.

- [ ] `CICD-008` **Wire frontend checks into CI**
  `[type: test]` `[size: S]`
  > Configure the CI stage to run `npm test`, `npx eslint .`, and `npx tsc --noEmit` for `frontend/` on every pull request.

- [ ] `CICD-009` **Configure CI secrets management**
  `[type: config]` `[size: S]`
  > Store DB credentials, JWT secret, and the GHCR push token as GitHub Actions secrets, consistent with the env-vars-only MVP secrets decision (no Vault) from `08_SECURITY_AND_VAULT.md` Part B.

- [ ] `CICD-010` **Add SAST and dependency-vulnerability scanning to CI**
  `[type: test]` `[size: S]`
  > Add a static-analysis (e.g. Semgrep) and dependency-vulnerability scan stage to `ci.yml`, per the SAST/DAST tooling list in `09_TESTING_STRATEGY.md` §8.

### 3. Identity & Access (Phase 1) — ⬜ 0 / 8 complete [░░░░░░░░░░] 0%

#### 3.1 Schema & Core Auth — ⬜ 0 / 4 complete

- [ ] `AUTH-001` **🔴 Write migration `V001__create_identity.sql`**
  `[type: migration]` `[size: S]`
  ⛔ BLOCKS: AUTH-002, AUTH-003
  > Create `identity.users`, `identity.roles`, and `identity.user_roles`, seeding the four roles `ADMIN`, `ARTISAN`, `CUSTOMER`, `B2B_BUYER`, per the identity schema table in `04_DATA_MODEL_AND_OWNERSHIP.md`.

- [ ] `AUTH-002` **🔴 Implement user registration with password hashing**
  `[type: feature]` `[size: M]`
  > Implement `POST /api/v1/auth/register`, storing a bcrypt hash in `identity.users.password_hash`, per the identity endpoints in `05_API_CONTRACTS.md` §2.1 and the password-handling controls in `08_SECURITY_AND_VAULT.md` Part C.

- [ ] `AUTH-003` **🔴 Implement JWT login**
  `[type: feature]` `[size: M]`
  > Implement `POST /api/v1/auth/login`, issuing a JWT bearer access token returned inside the `ApiResponse.data.accessToken` envelope, matching the response shape `infra/scripts/smoke-test.sh` already expects, per `08_SECURITY_AND_VAULT.md` Part C.

- [ ] `AUTH-004` **🔴 Define and implement the JWT lifetime/refresh policy**
  `[type: config]` `[size: S]`
  > Resolve the open question in `08_SECURITY_AND_VAULT.md` Part C (JWT lifetimes were left unspecified) by choosing and implementing a concrete access/refresh token expiry policy, and record the decision in code comments so it is discoverable without re-reading the planning docs.

#### 3.2 Authorization & Roles — ⬜ 0 / 3 complete

- [ ] `AUTH-005` **Implement the RBAC authorization filter**
  `[type: feature]` `[size: M]`
  > Implement a Spring Security filter enforcing the RBAC permission matrix in `08_SECURITY_AND_VAULT.md` §C.2, gating every endpoint by the caller's `identity.user_roles` membership using the per-service auth enforcement pattern from the same section.

- [ ] `AUTH-006` **Implement role assignment endpoints**
  `[type: feature]` `[size: S]`
  > Implement endpoints to grant/revoke rows in `identity.user_roles`, required by seller onboarding (SELLER-002, which grants `ARTISAN`) and B2B buyer onboarding (B2B-006, which grants `B2B_BUYER`).

- [ ] `AUTH-007` **Define the minimum ADMIN capability**
  `[type: feature]` `[size: S]`
  > Resolve the open question in `01_PRODUCT_SCOPE.md` (no admin functional requirements were defined) by specifying and implementing a minimum ADMIN capability set for MVP — at least user account suspension and seller verification (consumed by SELLER-003) — so the `ADMIN` role seeded in AUTH-001 has defined behavior somewhere in the system.

#### 3.3 Verification — ⬜ 0 / 1 complete

- [ ] `AUTH-008` **Write the identity domain test suite**
  `[type: test]` `[size: M]`
  > Implement the happy/edge/failure scenarios from `09_TESTING_STRATEGY.md` §3 for the identity module: duplicate-email registration, wrong-password login, expired/forged token rejection, and role-filter enforcement.

### 4. Seller Onboarding (Phase 1) — ⬜ 0 / 6 complete [░░░░░░░░░░] 0%

#### 4.1 Schema & Onboarding — ⬜ 0 / 3 complete

- [ ] `SELLER-001` **🔴 Write migration `V002__create_seller.sql`**
  `[type: migration]` `[size: S]`
  ⛔ BLOCKS: SELLER-002
  > Create `seller.sellers` (`user_id` FK to `identity.users`, `seller_type`, `display_name`, `verification_status`), per `04_DATA_MODEL_AND_OWNERSHIP.md`.

- [ ] `SELLER-002` **🔴 Implement seller registration**
  `[type: feature]` `[size: M]`
  > Implement `POST /api/v1/sellers`, creating a `seller.sellers` row linked to the calling user and granting the `ARTISAN` role via AUTH-006, per `05_API_CONTRACTS.md` §2.2.

- [ ] `SELLER-003` **Implement seller verification workflow**
  `[type: feature]` `[size: S]`
  > Implement `verification_status` transitions (`PENDING` → `VERIFIED`/`REJECTED`) via an endpoint restricted to `ADMIN` (AUTH-007), matching the RBAC matrix in `08_SECURITY_AND_VAULT.md` §C.2.

#### 4.2 Seller Profile & Tenancy — ⬜ 0 / 3 complete

- [ ] `SELLER-004` **Implement seller profile read/update endpoints**
  `[type: feature]` `[size: S]`
  > Implement `GET`/`PATCH` on a seller's own `seller.sellers` row, scoped to the authenticated user's own seller record only.

- [ ] `SELLER-005` **Implement row-level ownership enforcement**
  `[type: integration]` `[size: S]`
  > Implement and unit test the shared `seller_id = current user's seller row` ownership check consumed by CATALOG, INVENTORY, and PRICING endpoints, per the row-level tenancy model in `02_ARCHITECTURE_OVERVIEW.md` §5.

- [ ] `SELLER-006` **Write the seller domain test suite**
  `[type: test]` `[size: S]`
  > Cover duplicate seller registration, unverified-seller listing restriction, and ownership-check enforcement per `09_TESTING_STRATEGY.md` §3.

### 5. Catalog & Product Management (Phase 1) — ⬜ 0 / 8 complete [░░░░░░░░░░] 0%

#### 5.1 Schema & Core CRUD — ⬜ 0 / 3 complete

- [ ] `CATALOG-001` **🔴 Write migration `V003__create_catalog.sql`**
  `[type: migration]` `[size: S]`
  ⛔ BLOCKS: CATALOG-002
  > Create `catalog.products` with a status enum (`DRAFT`/`ACTIVE`/`ARCHIVED`), per `04_DATA_MODEL_AND_OWNERSHIP.md`.

- [ ] `CATALOG-002` **🔴 Implement product CRUD endpoints**
  `[type: feature]` `[size: L]`
  > Implement create/read/update/list endpoints for `catalog.products`, scoped by the seller-ownership check from SELLER-005, per `05_API_CONTRACTS.md` §2.3.

- [ ] `CATALOG-003` **Implement the product status lifecycle**
  `[type: feature]` `[size: M]`
  > Implement `DRAFT → ACTIVE → ARCHIVED` transitions, and enforce the hard constraint that AI may never write directly to `catalog.products` (`02_ARCHITECTURE_OVERVIEW.md` §7) — the only path into this table is the human-approval endpoint in AI-004.

#### 5.2 Discovery-Facing Reads — ⬜ 0 / 2 complete

- [ ] `CATALOG-004` **Implement category browsing endpoint**
  `[type: feature]` `[size: M]`
  > Implement `GET /api/v1/products` with a category filter, backing the mobile app's `CategoryBrowseScreen`.

- [ ] `CATALOG-005` **Implement product search/query endpoint**
  `[type: feature]` `[size: S]`
  > Add free-text search over `catalog.products.title`/`description` to the products listing endpoint.

#### 5.3 Extended Data & Verification — ⬜ 0 / 3 complete

- [ ] `CATALOG-006` **Add `catalog.product_attributes` child table**
  `[type: migration]` `[size: S]`
  > Extend the scaffold beyond its currently-documented gap of scaffolding only the primary aggregate root (per `docs/onboarding.md` §7) by adding `catalog.product_attributes` and its CRUD endpoints.

- [ ] `CATALOG-007` **Write the catalog domain test suite**
  `[type: test]` `[size: M]`
  > Cover the happy/edge/failure scenarios for catalog from `09_TESTING_STRATEGY.md` §3, including unauthorized cross-seller edit attempts.

- [ ] `CATALOG-008` **Add a regression test for the AI-write hard constraint**
  `[type: test]` `[size: S]`
  > Add an automated test proving every write to `catalog.products` originates only from a human-authenticated request path (CATALOG-002/003) or the explicit approval endpoint (AI-004) — never from an AI-job-processing code path — guarding constraint #3 in `CODEX_MEMORY.md`.

### 6. Media Management (Phase 1) — ⬜ 0 / 6 complete [░░░░░░░░░░] 0%

#### 6.1 Schema & Storage Adapter — ⬜ 0 / 2 complete

- [ ] `MEDIA-001` **🔴 Write migration `V004__create_media.sql`**
  `[type: migration]` `[size: S]`
  ⛔ BLOCKS: MEDIA-002
  > Create the `media` schema tables (metadata/URL references only), per `04_DATA_MODEL_AND_OWNERSHIP.md`.

- [ ] `MEDIA-002` **🔴 Implement media upload with a swappable storage adapter**
  `[type: feature]` `[size: L]`
  > Implement product-photo upload. Per the hard constraint in `02_ARCHITECTURE_OVERVIEW.md` §7, media binaries are never stored in PostgreSQL — only metadata/URLs live in the `media` schema; the actual bytes go to a local-disk `MediaStorageAdapter` implementation behind a swappable interface (principle #14), ahead of the deferred S3 migration noted in `MVP.md` §2.

#### 6.2 Media Lifecycle & Validation — ⬜ 0 / 4 complete

- [ ] `MEDIA-003` **Link media to catalog products**
  `[type: feature]` `[size: M]`
  > Implement associating one or more uploaded media rows with a `catalog.products` row, consumed by CATALOG-002's product detail responses.

- [ ] `MEDIA-004` **Implement media retrieval with expiring URLs**
  `[type: integration]` `[size: S]`
  > Implement a signed/expiring-URL retrieval pattern for uploaded media, per the media module's responsibility in `03_SERVICE_BOUNDARIES.md` §2.

- [ ] `MEDIA-005` **Define upload validation rules**
  `[type: config]` `[size: S]`
  > Define and enforce concrete max file size and allowed MIME type limits for uploads, since no thresholds were specified in the planning documents.

- [ ] `MEDIA-006` **Write the media domain test suite**
  `[type: test]` `[size: S]`
  > Cover oversized/invalid-type upload rejection and expiring-URL expiry, per `09_TESTING_STRATEGY.md` §3.

### 7. Inventory Management (Phase 1) — ⬜ 0 / 5 complete [░░░░░░░░░░] 0%

#### 7.1 Stock Tracking — ⬜ 0 / 3 complete

- [ ] `INV-001` **🔴 Write migration `V005__create_inventory.sql`**
  `[type: migration]` `[size: S]`
  ⛔ BLOCKS: INV-002
  > Create the `inventory` schema's stock-level table tied to `catalog.products`, per `04_DATA_MODEL_AND_OWNERSHIP.md`.

- [ ] `INV-002` **🔴 Implement stock CRUD and atomic decrement**
  `[type: feature]` `[size: M]`
  > Implement stock-quantity CRUD and an atomic decrement operation, consumed by order placement (COMM-003) to prevent overselling under concurrent checkouts.

- [ ] `INV-003` **Implement low-stock/out-of-stock status endpoint**
  `[type: feature]` `[size: S]`
  > Expose a stock-status field/endpoint consumed by CATALOG and MARKET listing responses.

#### 7.2 Verification — ⬜ 0 / 2 complete

- [ ] `INV-004` **Write the inventory domain test suite**
  `[type: test]` `[size: S]`
  > Include a concurrent-decrement race-condition test proving INV-002's atomic decrement never allows stock to go negative.

- [ ] `INV-005` **Implement inventory-aware marketplace listing**
  `[type: integration]` `[size: S]`
  > Ensure out-of-stock products are hidden or flagged in the MARKET browse endpoint (MARKET-003).

### 8. AI Digitization Pipeline (Phase 2) — ⬜ 0 / 10 complete [░░░░░░░░░░] 0%

#### 8.1 Job Schema & Provider Adapter — ⬜ 0 / 2 complete

- [ ] `AI-001` **🔴 Write migration `V006__create_ai.sql`**
  `[type: migration]` `[size: S]`
  ⛔ BLOCKS: AI-002
  > Create `ai.ai_jobs` (`job_type`, `status`, `input_ref`, `output_payload`, `reviewed_by`, `reviewed_at`), per `04_DATA_MODEL_AND_OWNERSHIP.md`'s ai schema and the polling-based async design in `07_QUEUE_AND_CACHE_DESIGN.md` Part A.

- [ ] `AI-003` **🔴 Define the `AiProviderAdapter` interface and ship `NoOpAiProviderAdapter`**
  `[type: integration]` `[size: L]`
  > Define a provider-agnostic `AiProviderAdapter` interface (principle #14) and ship the `NoOpAiProviderAdapter` placeholder implementation that processes `ai.ai_jobs` by polling, so a real AI/LLM provider can later be swapped in without touching any calling code.

#### 8.2 Voice-to-Catalog Journey — ⬜ 0 / 3 complete

- [ ] `AI-002` **🔴 Implement voice-capture upload endpoint**
  `[type: feature]` `[size: M]`
  > Implement the endpoint accepting a voice recording and creating an `ai.ai_jobs` row of type `VOICE_TO_LISTING`, per the Voice→Catalog sequence diagram in `06_COMMUNICATION_WORKFLOWS.md` §2.1.

- [ ] `AI-004` **🔴 Implement the human-approval endpoint for AI-drafted listings**
  `[type: feature]` `[size: L]`
  ⛔ BLOCKS: CATALOG-008
  > Implement the review/approve endpoint that lets a seller review a `VOICE_TO_LISTING` job's output and, only on explicit approval, writes the result into `catalog.products`. This is the enforcement point for the hard constraint that AI never writes directly to core entities.

- [ ] `AI-005` **Implement AI job status polling endpoint**
  `[type: feature]` `[size: M]`
  > Implement `GET /api/v1/ai/jobs/{id}` so the mobile client can poll `ai.ai_jobs` status until `PENDING` → `COMPLETED`, per the no-broker MVP decision (no push/webhook channel exists).

#### 8.3 AI Image Studio & Reliability — ⬜ 0 / 5 complete

- [ ] `AI-006` **Implement the AI Image Studio job type**
  `[type: feature]` `[size: L]`
  > Implement the `IMAGE_ENHANCEMENT` `ai.ai_jobs` job type per the AI Image Studio sequence diagram in `06_COMMUNICATION_WORKFLOWS.md` §2.2, feeding its approved output into MEDIA-002 on approval.

- [ ] `AI-007` **Implement idempotent AI job submission**
  `[type: integration]` `[size: S]`
  > Apply the idempotency-key pattern from `06_COMMUNICATION_WORKFLOWS.md` §5 so a retried job-submission request never creates a duplicate `ai.ai_jobs` row.

- [ ] `AI-008` **Implement AI job error/retry handling**
  `[type: integration]` `[size: S]`
  > Implement the retry/backoff behavior from `06_COMMUNICATION_WORKFLOWS.md` §4 for failed provider calls, marking a job `FAILED` only after retries are exhausted.

- [ ] `AI-009` **Write the AI domain test suite**
  `[type: test]` `[size: M]`
  > Cover successful transcription, provider timeout, and rejected-approval scenarios per `09_TESTING_STRATEGY.md` §3.

- [ ] `AI-010` **Document the real-provider swap-in configuration surface**
  `[type: config]` `[size: S]`
  > Document and stub the configuration (env vars, adapter bean selection) needed to replace `NoOpAiProviderAdapter` with a real AI provider without any code change elsewhere, per the provider-agnostic ADR in `16_ADR_PACK.md`.

### 9. AI-Assisted Pricing (Phase 2) — ⬜ 0 / 6 complete [░░░░░░░░░░] 0%

#### 9.1 Schema & AI Suggestion — ⬜ 0 / 3 complete

- [ ] `PRICE-001` **🔴 Write migration `V007__create_pricing.sql`**
  `[type: migration]` `[size: S]`
  ⛔ BLOCKS: PRICE-002
  > Create `pricing.sku_prices`, per `04_DATA_MODEL_AND_OWNERSHIP.md`.

- [ ] `PRICE-002` **🔴 Implement the AI Pricing job type**
  `[type: feature]` `[size: M]`
  > Implement the `PRICE_SUGGESTION` `ai.ai_jobs` job type per the AI Pricing sequence diagram in `06_COMMUNICATION_WORKFLOWS.md` §2.3.

- [ ] `PRICE-003` **🔴 Implement the human-approval endpoint for AI-suggested prices**
  `[type: feature]` `[size: M]`
  > Implement the approval workflow that writes an AI-suggested price into `pricing.sku_prices` only after explicit seller approval, enforcing the same AI-never-writes-directly hard constraint as AI-004, this time for the pricing schema.

#### 9.2 Manual Pricing & Audit — ⬜ 0 / 3 complete

- [ ] `PRICE-004` **Implement manual price entry (non-AI path)**
  `[type: feature]` `[size: S]`
  > Implement direct seller-authored price entry into `pricing.sku_prices` for sellers who skip AI pricing entirely.

- [ ] `PRICE-005` **Implement price history/audit trail**
  `[type: integration]` `[size: S]`
  > Implement an append-only history of changes to `pricing.sku_prices`, per the retention rules table in `04_DATA_MODEL_AND_OWNERSHIP.md` §5.

- [ ] `PRICE-006` **Write the pricing domain test suite**
  `[type: test]` `[size: S]`
  > Cover AI-suggestion approval, manual override, and the AI-write hard-constraint regression case, per `09_TESTING_STRATEGY.md` §3.

### 10. Marketplace & Discovery (Phase 3) — ⬜ 0 / 6 complete [░░░░░░░░░░] 0%

#### 10.1 Channels & Visibility — ⬜ 0 / 2 complete

- [ ] `MARKET-001` **🔴 Write migration `V008__create_market.sql`**
  `[type: migration]` `[size: S]`
  ⛔ BLOCKS: MARKET-002
  > Create `market.channels`, seeded with the B2C, B2B, and Government sales channels, per `04_DATA_MODEL_AND_OWNERSHIP.md` and consumed by `infra/scripts/seed.sh`.

- [ ] `MARKET-002` **🔴 Implement per-channel product visibility**
  `[type: feature]` `[size: M]`
  > Implement which `catalog.products` are visible in which `market.channels`, per the market module's responsibility in `03_SERVICE_BOUNDARIES.md` §2.

#### 10.2 Customer-Facing Browse — ⬜ 0 / 2 complete

- [ ] `MARKET-003` **Implement the marketplace browse/search endpoint**
  `[type: feature]` `[size: M]`
  > Implement the customer-facing `GET /api/v1/products` browse endpoint combining CATALOG, INVENTORY (INV-005), and MARKET visibility data, backing the mobile app's `HomeScreen` and `CategoryBrowseScreen`.

- [ ] `MARKET-006` **Implement channel-based access control**
  `[type: integration]` `[size: S]`
  > Enforce that B2B/Government channel data is only visible to users holding the corresponding role (`B2B_BUYER`) or channel entitlement.

#### 10.3 Government Channel & Verification — ⬜ 0 / 2 complete

- [ ] `MARKET-004` **Design the Government procurement-opportunity entity model**
  `[type: feature]` `[size: L]`
  > Resolve the open question flagged in `01_PRODUCT_SCOPE.md` and `06_COMMUNICATION_WORKFLOWS.md` §2.6 (no procurement-opportunity entity model was defined) by migrating a `market.procurement_opportunities` table and the endpoints to list and respond to opportunities.

- [ ] `MARKET-005` **Write the marketplace domain test suite**
  `[type: test]` `[size: S]`
  > Cover channel-visibility filtering and cross-channel access-control enforcement, per `09_TESTING_STRATEGY.md` §3.

### 11. Commerce Core — Cart, Checkout & Orders (Phase 3) — ⬜ 0 / 8 complete [░░░░░░░░░░] 0%

#### 11.1 Schema & Cart — ⬜ 0 / 2 complete

- [ ] `COMM-001` **🔴 Write migration `V010__create_commerce.sql`**
  `[type: migration]` `[size: S]`
  ⛔ BLOCKS: COMM-002
  > Create `commerce.carts`, `commerce.orders`, and `commerce.order_items`, per `04_DATA_MODEL_AND_OWNERSHIP.md`.

- [ ] `COMM-002` **🔴 Implement cart management endpoints**
  `[type: feature]` `[size: L]`
  > Implement add/remove/update-quantity endpoints against `commerce.carts`, scoped to the authenticated buyer.

#### 11.2 Checkout & Order Lifecycle — ⬜ 0 / 3 complete

- [ ] `COMM-003` **🔴 Implement checkout/order-placement**
  `[type: feature]` `[size: L]`
  > Implement `POST /api/v1/orders`, converting a cart into a `commerce.orders` row and atomically decrementing stock via INV-002, per the B2C Purchase sequence diagram in `06_COMMUNICATION_WORKFLOWS.md` §2.4.

- [ ] `COMM-004` **🔴 Add missing order statuses and implement the status lifecycle**
  `[type: feature]` `[size: M]`
  > Resolve the open question that no `CANCELLED`/`RETURNED` order status exists despite refunds being modeled elsewhere (`01_PRODUCT_SCOPE.md`, `04_DATA_MODEL_AND_OWNERSHIP.md`) by adding both statuses to the order-status enum and implementing their transition endpoints.

- [ ] `COMM-006` **Implement idempotent order placement**
  `[type: integration]` `[size: S]`
  > Apply the idempotency-key pattern from `06_COMMUNICATION_WORKFLOWS.md` §5 to checkout so a retried request never double-charges a buyer or double-decrements stock.

#### 11.3 Order Servicing — ⬜ 0 / 3 complete

- [ ] `COMM-005` **Implement order history/detail endpoints**
  `[type: feature]` `[size: S]`
  > Implement buyer-facing and seller-facing order history/detail endpoints, backing the mobile app's `OrderDetailScreen`.

- [ ] `COMM-008` **Implement refund initiation**
  `[type: feature]` `[size: S]`
  > Implement the refund-initiation endpoint referenced by the payment/commerce relationship in the data model, feeding PAY-003's refund processing.

- [ ] `COMM-007` **Write the commerce domain test suite**
  `[type: test]` `[size: M]`
  > Cover cart-to-order conversion, overselling prevention, and the new cancelled/returned-status transitions, per `09_TESTING_STRATEGY.md` §3.

### 12. Payments (Phase 3) — ⬜ 0 / 6 complete [░░░░░░░░░░] 0%

#### 12.1 Schema & Gateway Adapter — ⬜ 0 / 2 complete

- [ ] `PAY-001` **🔴 Write migration `V011__create_payment.sql`**
  `[type: migration]` `[size: S]`
  ⛔ BLOCKS: PAY-002
  > Create `payment.transactions`, per `04_DATA_MODEL_AND_OWNERSHIP.md`.

- [ ] `PAY-002` **🔴 Define the `PaymentGatewayAdapter` interface and ship `MockPaymentGatewayAdapter`**
  `[type: integration]` `[size: L]`
  > Define a provider-agnostic `PaymentGatewayAdapter` interface and ship `MockPaymentGatewayAdapter`, a non-production placeholder (principle #14), matching the deferred real-payment-gateway entry in `MVP.md` §2, so a real gateway can later be swapped in without touching calling code.

#### 12.2 Payment Flows — ⬜ 0 / 4 complete

- [ ] `PAY-003` **Implement payment capture and refund endpoints**
  `[type: feature]` `[size: M]`
  > Implement endpoints that call `PaymentGatewayAdapter` to capture payment on order placement (COMM-003) and to process refunds (COMM-008), recording every result in `payment.transactions`.

- [ ] `PAY-004` **Implement a payment-callback endpoint stub**
  `[type: feature]` `[size: S]`
  > Implement the endpoint shape for gateway callbacks, even though `05_API_CONTRACTS.md` §5 notes no webhooks currently exist, so `MockPaymentGatewayAdapter` has a real integration point to call into.

- [ ] `PAY-006` **Document the real-gateway swap-in configuration surface**
  `[type: config]` `[size: S]`
  > Document the configuration needed to replace `MockPaymentGatewayAdapter` with a real gateway, mirroring AI-010.

- [ ] `PAY-005` **Write the payments domain test suite**
  `[type: test]` `[size: S]`
  > Cover declined payment, gateway timeout, and refund processing, per `09_TESTING_STRATEGY.md` §3.

### 13. B2B & Government Channel (Phase 4) — ⬜ 0 / 8 complete [░░░░░░░░░░] 0%

#### 13.1 Schema & Status Vocabulary — ⬜ 0 / 2 complete

- [ ] `B2B-001` **🔴 Write migration `V009__create_b2b.sql`**
  `[type: migration]` `[size: S]`
  ⛔ BLOCKS: B2B-003
  > Create `b2b.inquiries`, per `04_DATA_MODEL_AND_OWNERSHIP.md`.

- [ ] `B2B-002` **🔴 Define the B2B inquiry/quote status vocabulary**
  `[type: config]` `[size: S]`
  > Resolve the open question that no status vocabulary was enumerated in `01_PRODUCT_SCOPE.md` by defining an explicit enum (`OPEN`, `RESPONDED`, `QUOTED`, `ACCEPTED`, `DECLINED`, `CLOSED`) and applying it to `b2b.inquiries.status`.

#### 13.2 Inquiry Workflow — ⬜ 0 / 3 complete

- [ ] `B2B-003` **🔴 Implement inquiry submission (buyer)**
  `[type: feature]` `[size: L]`
  > Implement `POST /api/v1/b2b/inquiries` per the B2B sequence diagram in `06_COMMUNICATION_WORKFLOWS.md` §2.5, backing the mobile app's `InquiryListScreen` and `InquiryDetailScreen` for a `B2B_BUYER`.

- [ ] `B2B-004` **Implement inquiry response (seller)**
  `[type: feature]` `[size: M]`
  > Implement the seller-side response/quote endpoint, giving `InquiryListScreen`'s "incoming inquiries" view (the `isSeller` branch) real data to act on.

- [ ] `B2B-005` **Implement inquiry-to-order conversion**
  `[type: feature]` `[size: M]`
  > Implement converting an `ACCEPTED` `b2b.inquiries` row into a `commerce.orders` row via COMM-003.

#### 13.3 Onboarding & Government Extension — ⬜ 0 / 3 complete

- [ ] `B2B-006` **Implement B2B buyer verification/onboarding**
  `[type: feature]` `[size: S]`
  > Implement the account-verification step required before a user can submit B2B inquiries, mirroring SELLER-003's verification pattern, and granting the `B2B_BUYER` role via AUTH-006.

- [ ] `B2B-008` **Extend the inquiry flow for the Government channel**
  `[type: feature]` `[size: M]`
  > Extend inquiry submission/response to support the Government procurement channel (MARKET-004), including any additional fields a procurement response requires.

- [ ] `B2B-007` **Write the B2B domain test suite**
  `[type: test]` `[size: M]`
  > Cover the full inquiry lifecycle and Government-channel variant, per `09_TESTING_STRATEGY.md` §3.

### 14. Fulfillment — PROPOSED (Phase 5) — ⬜ 0 / 4 complete [░░░░░░░░░░] 0%

#### 14.1 Approval Gate — ⬜ 0 / 1 complete

- [ ] `FUL-001` **Obtain team approval before starting fulfillment work**
  `[type: config]` `[size: S]`
  ⛔ BLOCKS: FUL-002, FUL-003, FUL-004
  > This entire domain is explicitly PROPOSED and pending team approval per `01_PRODUCT_SCOPE.md` §23 and the deferred-scope table in `MVP.md` §2. Before any other FUL task begins, obtain and record explicit team sign-off on scope; until then this section stays unstarted.

#### 14.2 Fulfillment Implementation (post-approval) — ⬜ 0 / 3 complete

- [ ] `FUL-002` **Write the fulfillment schema migration**
  `[type: migration]` `[size: M]`
  > Once FUL-001 is approved, implement the fulfillment schema migration per the PROPOSED entity model in `04_DATA_MODEL_AND_OWNERSHIP.md`'s fulfillment section.

- [ ] `FUL-003` **Implement shipment tracking endpoints**
  `[type: feature]` `[size: L]`
  > Implement the PROPOSED shipment-tracking endpoints described in `05_API_CONTRACTS.md`'s fulfillment section, once FUL-002 has landed.

- [ ] `FUL-004` **Write the fulfillment domain test suite**
  `[type: test]` `[size: S]`
  > Cover the PROPOSED fulfillment scenarios once FUL-003 lands.

### 15. Experience — PROPOSED — ⬜ 0 / 3 complete [░░░░░░░░░░] 0%

#### 15.1 Approval Gate & Implementation — ⬜ 0 / 3 complete

- [ ] `EXP-001` **Obtain team approval before starting experience work**
  `[type: config]` `[size: S]`
  ⛔ BLOCKS: EXP-002
  > This domain is explicitly PROPOSED and pending team approval per `01_PRODUCT_SCOPE.md` §24. Before any other EXP task begins, obtain and record explicit team sign-off on scope.

- [ ] `EXP-002` **Implement the experience module**
  `[type: feature]` `[size: L]`
  > Once EXP-001 is approved, implement the PROPOSED experience schema and endpoints described in `04_DATA_MODEL_AND_OWNERSHIP.md`'s experience section.

- [ ] `EXP-003` **Write the experience domain test suite**
  `[type: test]` `[size: S]`
  > Cover the PROPOSED experience scenarios once EXP-002 lands.

### 16. Frontend Mobile App (Phase 6) — ⬜ 0 / 16 complete [░░░░░░░░░░] 0%

#### 16.1 App Foundation — ⬜ 0 / 4 complete

- [ ] `FE-001` **🔴 Scaffold the Expo/React Native project**
  `[type: infra]` `[size: M]`
  ⛔ BLOCKS: FE-002
  > Initialize the `frontend/` Expo app with TypeScript, React Navigation, React Query, and Zustand, per `13_FRONTEND_DASHBOARD_PLAN.md` §4 and the `frontend/src` layout in `15_REPOSITORY_STRUCTURE.md`.

- [ ] `FE-002` **🔴 Implement auth screens and token storage**
  `[type: feature]` `[size: M]`
  > Implement login/register screens and secure JWT storage plus refresh handling, per the frontend auth flow in `08_SECURITY_AND_VAULT.md` Part C.

- [ ] `FE-003` **🔴 Decide and document the design system**
  `[type: config]` `[size: S]`
  > Resolve the design-system choice between React Native Paper (reasoned in `13_FRONTEND_DASHBOARD_PLAN.md` §7) and a custom lightweight component library, and record the decision so every subsequent frontend task builds on one consistent system.

- [ ] `FE-004` **Implement navigation/routing**
  `[type: feature]` `[size: M]`
  > Implement the full page hierarchy and persona-conditional bottom-tab/stack routing (Artisan vs Customer vs B2B Buyer) per `13_FRONTEND_DASHBOARD_PLAN.md` §2.

#### 16.2 Marketplace & Catalog Screens — ⬜ 0 / 4 complete

- [ ] `FE-005` **Implement the Home/Marketplace screen**
  `[type: feature]` `[size: M]`
  > Implement the persona-differentiated home screen ("My Products" for Artisan, "Marketplace" for Customer) consuming CATALOG-002/MARKET-003.

- [ ] `FE-006` **Implement category browse and search screens**
  `[type: feature]` `[size: M]`
  > Implement screens consuming CATALOG-004/CATALOG-005.

- [ ] `FE-007` **Implement the product detail screen**
  `[type: feature]` `[size: M]`
  > Implement a product detail screen consuming CATALOG-002 and MEDIA-004.

- [ ] `FE-013` **Implement seller product-management screens**
  `[type: feature]` `[size: S]`
  > Implement create/edit/list screens for a seller's own products.

#### 16.3 AI-Assisted Screens — ⬜ 0 / 2 complete

- [ ] `FE-008` **Implement voice-capture and AI-listing-review screens**
  `[type: feature]` `[size: L]`
  > Implement the screens driving AI-002 (voice capture) and AI-004 (approval), completing the Voice→Catalog journey end to end in the app.

- [ ] `FE-009` **Implement the AI Image Studio screen**
  `[type: feature]` `[size: M]`
  > Implement a screen driving AI-006's `IMAGE_ENHANCEMENT` job type.

#### 16.4 Commerce, B2B & Account Screens — ⬜ 0 / 5 complete

- [ ] `FE-010` **Implement cart and checkout screens**
  `[type: feature]` `[size: M]`
  > Implement screens consuming COMM-002/COMM-003.

- [ ] `FE-011` **Implement order history and order detail screens**
  `[type: feature]` `[size: M]`
  > Implement screens consuming COMM-005.

- [ ] `FE-012` **Implement B2B inquiry list and detail screens**
  `[type: feature]` `[size: M]`
  > Implement screens consuming B2B-003/B2B-004.

- [ ] `FE-014` **Implement the user profile/settings screen**
  `[type: feature]` `[size: S]`
  > Implement a screen consuming SELLER-004/AUTH endpoints for profile viewing and editing.

- [ ] `FE-015` **Implement the documented ADMIN fallback view**
  `[type: feature]` `[size: S]`
  > Since `13_FRONTEND_DASHBOARD_PLAN.md` notes no dashboard is defined for `ADMIN`, implement the documented fallback (an `ADMIN`-only user sees the marketplace view) as explicit, tested behavior rather than an accident of routing.

#### 16.5 Verification — ⬜ 0 / 1 complete

- [ ] `FE-016` **Write the frontend test suite**
  `[type: test]` `[size: M]`
  > Implement Jest unit/component tests for the screens above, per the frontend tier of the test pyramid in `09_TESTING_STRATEGY.md`.

### 17. Observability & Incident Readiness — ⬜ 0 / 8 complete [░░░░░░░░░░] 0%

#### 17.1 Metrics, Logging & Tracing — ⬜ 0 / 4 complete

- [ ] `OBS-001` **🔴 Wire Actuator + Micrometer + Prometheus**
  `[type: infra]` `[size: M]`
  > Expose Prometheus-formatted metrics via Micrometer, per the observability tooling table in `11_OBSERVABILITY_AND_INCIDENTS.md` Part A.

- [ ] `OBS-002` **Build Grafana dashboards**
  `[type: infra]` `[size: M]`
  > Build dashboards for the per-module RED (Rate/Errors/Duration) metrics table in Part A.

- [ ] `OBS-003` **Implement structured logging**
  `[type: config]` `[size: S]`
  > Implement the structured JSON logging schema (fields, correlation ID) from Part A across every backend module.

- [ ] `OBS-004` **Implement distributed tracing**
  `[type: infra]` `[size: M]`
  > Implement the tracing strategy from Part A, propagating spans across the single deployable's module boundaries and outbound adapter calls (AI, payment).

#### 17.2 Alerting & Incident Process — ⬜ 0 / 4 complete

- [ ] `OBS-005` **Define SLOs and alerting rules**
  `[type: config]` `[size: S]`
  > Define and wire the SLO thresholds and alert-rules table from Part A into Prometheus/Grafana.

- [ ] `OBS-006` **Operationalize the incident severity matrix**
  `[type: config]` `[size: S]`
  > Document and operationalize the P0–P3 severity matrix, detection sources, and alert routing from Part B.

- [ ] `OBS-007` **Document the on-call and incident lifecycle process**
  `[type: config]` `[size: S]`
  > Document the on-call rotation approach and the detect→triage→mitigate→resolve→postmortem lifecycle from Part B's diagram.

- [ ] `OBS-008` **Create the postmortem template**
  `[type: config]` `[size: S]`
  > Create the postmortem document template referenced in Part B, for use after any P0/P1 incident.

### 18. Security & Resilience Hardening — ⬜ 0 / 10 complete [░░░░░░░░░░] 0%

#### 18.1 Threat Model & Secrets — ⬜ 0 / 4 complete

- [ ] `SEC-001` **🔴 Formalize the threat model and asset classification**
  `[type: config]` `[size: M]`
  > Turn the threat model and asset-classification table from `08_SECURITY_AND_VAULT.md` Part A into a living security doc, classifying every data asset (passwords, card-data references, PII) by sensitivity.

- [ ] `SEC-002` **Apply STRIDE-driven hardening per module**
  `[type: config]` `[size: M]`
  > For each of the 13 modules, implement the mitigations implied by the per-module STRIDE table in Part A §A.4 (input validation, authorization checks, audit logging).

- [ ] `SEC-003` **🔴 Implement env-vars-only secrets management**
  `[type: config]` `[size: S]`
  > Implement the env-vars-only secrets approach from Part B, ensuring no secret is ever committed, and document the PROPOSED Vault mount structure for a future migration.

- [ ] `SEC-004` **Define and implement rate-limiting thresholds**
  `[type: config]` `[size: S]`
  > Resolve the open question that no rate-limiting thresholds were specified (`05_API_CONTRACTS.md` §4, PROPOSED) by choosing and implementing concrete per-endpoint limits.

#### 18.2 Resilience Patterns — ⬜ 0 / 4 complete

- [ ] `SEC-005` **Implement circuit breakers per outbound integration**
  `[type: feature]` `[size: M]`
  > Implement Resilience4j circuit breakers around the AI provider adapter (AI-003) and payment gateway adapter (PAY-002) calls, per the config table in `12_FAILURE_RESILIENCE_PLAN.md` §2.

- [ ] `SEC-006` **Implement retry policies**
  `[type: feature]` `[size: S]`
  > Implement the retry-policy table from §3 for transient failures in outbound adapter calls.

- [ ] `SEC-007` **Implement graceful degradation patterns**
  `[type: feature]` `[size: M]`
  > Implement the degradation patterns from §4 (e.g. serve cached/stale catalog data if a dependency is unavailable) using the in-memory Caffeine cache decision from `07_QUEUE_AND_CACHE_DESIGN.md` Part B.

- [ ] `SEC-008` **Configure bulkheads and timeouts**
  `[type: config]` `[size: S]`
  > Configure connection-pool bulkheads and request timeouts per §5.

#### 18.3 Chaos & Disaster Recovery — ⬜ 0 / 2 complete

- [ ] `SEC-009` **Implement chaos-engineering test scenarios**
  `[type: test]` `[size: M]`
  > Implement the chaos scenarios from §6 (e.g. kill the database connection mid-request) as scripted local tests, verifying the graceful-degradation behavior from SEC-007.

- [ ] `SEC-010` **Document DR RTO/RPO and recovery procedures**
  `[type: config]` `[size: S]`
  > Document the disaster-recovery RTO/RPO targets and per-failure-class recovery procedures from §7-8.

### 19. Testing & Quality Gates (Phase 7) — ⬜ 0 / 12 complete [░░░░░░░░░░] 0%

#### 19.1 Coverage Roll-Up & Gates — ⬜ 0 / 2 complete

- [ ] `TEST-001` **🔴 Track aggregate per-domain integration test completion**
  `[type: test]` `[size: L]`
  > Use this task as the roll-up checkpoint for the 13 per-module Testcontainers-backed integration test suites itemized individually in each domain section (AUTH-008, SELLER-006, CATALOG-007, MEDIA-006, INV-004, AI-009, PRICE-006, MARKET-005, COMM-007, PAY-005, B2B-007, FUL-004, EXP-003) — mark complete only once every one of those is complete.

- [ ] `TEST-002` **🔴 Enforce CI gate thresholds**
  `[type: config]` `[size: S]`
  > Configure the CI pipeline (CICD-001) to enforce the coverage/pass-rate thresholds from `09_TESTING_STRATEGY.md` §6, failing the build below them.

#### 19.2 Non-Functional & Cross-Cutting Tests — ⬜ 0 / 7 complete

- [ ] `TEST-003` **Implement the k6 performance test suite**
  `[type: test]` `[size: L]`
  > Implement k6 load-test scripts against the SLO table in §7 for the highest-traffic endpoints (catalog browse, checkout).

- [ ] `TEST-004` **Verify OWASP Top 10 / SAST-DAST coverage**
  `[type: test]` `[size: M]`
  > Verify coverage of the OWASP Top 10 mapping and SAST/DAST tooling from §8, wiring results into CICD-010.

- [ ] `TEST-005` **Implement API contract tests**
  `[type: test]` `[size: M]`
  > Implement request/response contract tests against the endpoint catalogue in `05_API_CONTRACTS.md`, catching drift between docs and implementation.

- [ ] `TEST-006` **Implement idempotency test coverage**
  `[type: test]` `[size: S]`
  > Add automated tests proving idempotent behavior for every endpoint listed in the idempotency table in `06_COMMUNICATION_WORKFLOWS.md` §5 (order placement, AI job submission).

- [ ] `TEST-007` **Implement row-level tenancy isolation tests**
  `[type: test]` `[size: S]`
  > Prove, with automated tests, that no user can read or write another seller's or buyer's rows in any module, per the row-level ownership model in `02_ARCHITECTURE_OVERVIEW.md` §5.

- [ ] `TEST-010` **Implement hard-constraint regression tests**
  `[type: test]` `[size: S]`
  > Add a permanent regression suite asserting all six hard constraints from `02_ARCHITECTURE_OVERVIEW.md` §7 continue to hold.

- [ ] `TEST-011` **Verify migration rollback behavior**
  `[type: test]` `[size: S]`
  > Verify each Flyway migration's expand/contract reversibility per the migration strategy in `04_DATA_MODEL_AND_OWNERSHIP.md` §4.

#### 19.3 End-to-End & Release Verification — ⬜ 0 / 3 complete

- [ ] `TEST-008` **Expand the end-to-end smoke test**
  `[type: test]` `[size: M]`
  > Extend `infra/scripts/smoke-test.sh`'s actuator/login/catalog check into a fuller journey covering voice-capture-to-purchase, and run it as a CI stage.

- [ ] `TEST-009` **Implement frontend end-to-end tests**
  `[type: test]` `[size: S]`
  > Implement mobile end-to-end tests (e.g. Detox or Maestro) covering login, browse, purchase, and B2B inquiry on an Android emulator.

- [ ] `TEST-012` **Verify the release Definition of Done**
  `[type: test]` `[size: M]`
  > Implement an automated or checklist-driven verification of the task/feature/release-level Definition of Done from `14_IMPLEMENTATION_ROADMAP.md` §6 before each release.

### 20. Documentation & Release Readiness — ⬜ 0 / 6 complete [░░░░░░░░░░] 0%

#### 20.1 Living Documentation — ⬜ 0 / 3 complete

- [ ] `DOC-001` **Keep repository documentation current**
  `[type: config]` `[size: S]`
  > Update `REPOSITORY_STRUCTURE.md` and `docs/onboarding.md` whenever the repo layout changes, per `15_REPOSITORY_STRUCTURE.md` §6.

- [ ] `DOC-002` **Publish the OpenAPI spec**
  `[type: config]` `[size: S]`
  > Generate and publish the springdoc-driven OpenAPI/Swagger spec at `/swagger-ui/index.html`, per `05_API_CONTRACTS.md` §6, and check the generated spec into `docs/api/`.

- [ ] `DOC-003` **Establish the ADR maintenance process**
  `[type: config]` `[size: S]`
  > Establish the process for appending new ADRs to `16_ADR_PACK.md` as future decisions are made (e.g. deployment provider, real AI provider).

#### 20.2 Open Questions & Release Gates — ⬜ 0 / 3 complete

- [ ] `DOC-004` **🔴 Compile and drive the open-questions register to closure**
  `[type: feature]` `[size: M]`
  > Compile every "⚠️ Open Question" block across all 20 planning documents into a single tracked register (for any not already resolved by a dedicated task above), and drive each to an explicit decision before Phase 7 release sign-off.

- [ ] `DOC-006` **Decide the deployment provider/target**
  `[type: config]` `[size: S]`
  > Resolve the placeholder in `.github/workflows/cd.yml` (no deployment target chosen) by selecting a concrete deployment provider/target and completing the deploy step (CICD-002).

- [ ] `DOC-005` **Execute the staged release strategy**
  `[type: config]` `[size: S]`
  > Execute the three-stage release strategy (Internal Alpha → Hackathon Demo → Post-Hackathon) from `14_IMPLEMENTATION_ROADMAP.md` §7, gating each stage on its exit criteria.
