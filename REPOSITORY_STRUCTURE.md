# Repository Structure — Artisan Digital Commerce Platform

> **Reconciliation note (read first):** the scaffold-generation brief this document was requested under assumed a microservices topology (one independently-deployed Spring Boot app + Dockerfile + Kubernetes manifests per domain), RabbitMQ, Redis, Vault, and a React + Vite web frontend. Per the explicit decision recorded when this scaffold was commissioned, this structure instead matches what the 17 planning documents actually specify: **ADR-1** (modular monolith — one Spring Boot deployable), **ADR-3**/**ADR-4** (no message broker, no dedicated cache for MVP), **ADR-10** (no Vault for MVP — environment variables instead), and **ADR-7** (React Native + Expo mobile app, not a React web SPA). Kubernetes manifests are omitted entirely because `02_ARCHITECTURE_OVERVIEW.md` §4 and `10_CI_CD_AND_ENVIRONMENTS.md` §A.3 explicitly leave the deployment/orchestration target undecided ("a suitable cloud/server environment") — inventing Kubernetes-specific infrastructure would itself be inventing a decision the workflow never made. Where the original brief's file/folder shapes are still useful (multi-module Maven, shared libraries, per-domain packages, CI stages, docker-compose local stack), they are kept, adapted to a single-deployable reality.

## Monorepo vs. Polyrepo

Monorepo — explicit in the source workflow (§35) and reaffirmed in `15_REPOSITORY_STRUCTURE.md` §1. One repository, one Maven reactor for the backend, one Expo project for the mobile app, both versioned together.

## Full Annotated Directory Tree

```text
artisan-platform/                        # Monorepo root
├── pom.xml                              # See note below — NOT at true root; Maven reactor root lives at backend/pom.xml
├── .gitignore                           # Java/Maven, Node/Expo, IDE, .env*, Docker, OS ignores
├── .editorconfig                        # Consistent formatting: Java, TS, JSON, YAML, Markdown
├── README.md                            # Project overview, tech stack, quickstart, doc links
├── Makefile                             # install/dev/build/test/lint/format/docker-*/migrate/seed/logs/health targets
├── docker-compose.yml                   # Local stack: PostgreSQL + backend app (NO RabbitMQ/Redis/Vault — not in the approved architecture)
├── docker-compose.override.yml          # Dev overrides: hot-reload volume mounts, debug port, verbose logging
├── .env.example                         # Every env var used platform-wide, grouped, placeholder values only
├── REPOSITORY_STRUCTURE.md              # This file
│
├── backend/                             # Spring Boot multi-module Maven reactor — ONE deployable JAR (modular monolith, ADR-1)
│   ├── pom.xml                          # Parent/reactor POM: packaging=pom, dependencyManagement (Spring Boot BOM), pluginManagement, declares all modules below
│   ├── common/                          # Shared library module: cross-cutting code every domain module depends on
│   │   ├── pom.xml
│   │   └── src/main/java/com/artisanplatform/common/
│   │       ├── response/                # ApiResponse<T> generic envelope
│   │       ├── exception/                # Base exception classes + shared error codes
│   │       ├── security/                 # JwtUtil (token issue/validate/parse claims)
│   │       ├── logging/                  # CorrelationIdFilter (X-Correlation-Id -> MDC)
│   │       ├── audit/                    # Auditable base entity (createdAt/updatedAt)
│   │       └── constants/                # GlobalConstants (roles, market channels, etc.)
│   ├── test-support/                     # Shared test library module
│   │   ├── pom.xml
│   │   └── src/main/java/com/artisanplatform/testsupport/
│   │       ├── containers/               # Shared Testcontainers PostgreSQL setup
│   │       ├── BaseIntegrationTest.java   # Abstract base every *IntegrationTest extends
│   │       └── fixtures/                 # Shared test data builders
│   ├── modules/                          # One Maven module per domain from 03_SERVICE_BOUNDARIES.md — JARs, NOT independently runnable
│   │   ├── auth/                         # identity schema — users, roles, user_roles, addresses
│   │   ├── seller/                       # seller schema — sellers, artisan_profiles
│   │   ├── catalog/                      # catalog schema — categories, products, product_skus, product_attributes
│   │   ├── media/                        # media schema — media_assets, product_media + MediaStorageService abstraction
│   │   ├── inventory/                    # inventory schema — inventories, inventory_movements
│   │   ├── ai/                           # ai schema — ai_jobs + every AI intermediate result + provider-agnostic adapter interface
│   │   ├── pricing/                      # pricing schema — cost_records, market_prices, sku_prices
│   │   ├── market/                       # market schema — market_channels, market_listings, external_marketplaces, external_listings
│   │   ├── b2b/                          # b2b schema — b2b_buyers, b2b_inquiries, b2b_quotations, purchase_orders
│   │   ├── commerce/                     # commerce schema — carts, cart_items, orders, order_items, order_status_history
│   │   └── payment/                      # payment schema — payments, payment_transactions, refunds, seller_settlements, invoices + gateway adapter interface
│   │       # (each module/<domain>/ follows the identical internal shape shown once, below)
│   └── app/                              # THE runnable Spring Boot application — aggregates every module above into one deployable
│       ├── pom.xml                       # Depends on common + every modules/* — produces the single executable JAR
│       ├── Dockerfile                    # Multi-stage Maven build -> eclipse-temurin:21-jre-alpine runtime, non-root, healthcheck
│       └── src/main/
│           ├── java/com/artisanplatform/app/
│           │   ├── ArtisanPlatformApplication.java   # @SpringBootApplication, component-scans com.artisanplatform
│           │   └── config/
│           │       ├── SecurityConfig.java            # SecurityFilterChain, JWT filter, public vs protected routes, CORS
│           │       └── SwaggerConfig.java              # OpenAPI bean, JWT bearer scheme
│           └── resources/
│               ├── application.yml                    # Shared defaults: app name, port 8080, Flyway, Actuator, JSON log pattern
│               ├── application-local.yml               # docker-compose service-name datasource, DEBUG logging, show-sql
│               ├── application-staging.yml
│               └── application-prod.yml
│
├── modules/<domain>/                     # ONE internal shape, applies to all 11 modules listed above — shown once here
│   ├── pom.xml                           # Parent: backend/pom.xml; artifactId: <domain>; only the Spring Boot starters this domain actually needs
│   └── src/
│       ├── main/java/com/artisanplatform/<domain>/
│       │   ├── controller/               # [Domain]Controller — one method per endpoint in 05_API_CONTRACTS.md for this domain
│       │   ├── service/                  # [Domain]Service interface + impl/[Domain]ServiceImpl (constructor injection)
│       │   ├── repository/               # [Domain]Repository extends JpaRepository
│       │   ├── domain/
│       │   │   ├── entity/               # JPA @Entity classes for every table this module owns (04_DATA_MODEL_AND_OWNERSHIP.md)
│       │   │   ├── dto/request|response/  # Request/response DTOs matching 05_API_CONTRACTS.md schemas
│       │   │   └── mapper/                # MapStruct [Domain]Mapper
│       │   ├── exception/                 # Domain-specific exception + this module's slice of GlobalExceptionHandler
│       │   └── client/ or adapter/         # ONLY in ai/ (AI provider adapter) and payment/ (gateway adapter) and media/ (MediaStorageService) — principle #14 isolation
│       └── test/
│           ├── java/com/artisanplatform/<domain>/
│           │   ├── unit/service/          # [Domain]ServiceTest — Mockito
│           │   ├── unit/controller/        # [Domain]ControllerTest — @WebMvcTest
│           │   └── integration/            # [Domain]IntegrationTest — Testcontainers, extends BaseIntegrationTest
│           └── resources/application-test.yml
│
├── database/
│   └── migrations/                       # Flyway, versioned, applies to the ONE shared `artisan_marketplace` database — source §35/§36 (root-level, not under backend/)
│       ├── V001__create_identity.sql
│       ├── V002__create_seller.sql
│       ├── V003__create_catalog.sql
│       ├── V004__create_media.sql
│       ├── V005__create_inventory.sql
│       ├── V006__create_ai.sql
│       ├── V007__create_pricing.sql
│       ├── V008__create_market.sql
│       ├── V009__create_b2b.sql
│       ├── V010__create_commerce.sql
│       └── V011__create_payment.sql
│       # V012 (fulfillment) / V013 (experience) intentionally NOT generated — PROPOSED, pending team approval (source §23/§24)
│
├── frontend/                              # React Native + TypeScript + Expo — Android-first mobile app (ADR-7). NOT a web SPA.
│   ├── package.json
│   ├── app.json                          # Expo app config (name, slug, android package id)
│   ├── babel.config.js
│   ├── tsconfig.json
│   ├── .eslintrc.cjs
│   ├── .prettierrc
│   ├── App.tsx                            # Root component: providers (QueryClient, auth) + navigation
│   ├── index.ts                           # Expo entry point (registerRootComponent)
│   └── src/
│       ├── api/
│       │   ├── client.ts                 # Axios instance, JWT + correlation-id interceptors, 401/403/5xx handling
│       │   └── endpoints.ts               # Every endpoint constant/function from 05_API_CONTRACTS.md, grouped by domain
│       ├── navigation/
│       │   └── index.tsx                 # Stack/tab navigators, AuthGuard, RoleGuard, per 13_FRONTEND_DASHBOARD_PLAN.md §2
│       ├── store/
│       │   └── auth.ts                   # Zustand store: user/token/refreshToken/isAuthenticated + SecureStore persistence
│       ├── components/
│       │   ├── ui/                       # Button, Input, Card, Modal, Badge, Spinner, Toast, Table (React Native primitives)
│       │   └── shared/                   # ScreenLayout, AuthGuard, ErrorBoundary, EmptyState
│       ├── screens/                       # auth/, artisan/, marketplace/, b2b/, commerce/, payment/ — per persona (13_FRONTEND_DASHBOARD_PLAN.md §1-2)
│       ├── hooks/                         # Shared hooks (useAuth, etc.)
│       └── types/index.ts                 # Shared TS types mirroring API DTOs
│
├── infra/
│   └── scripts/
│       ├── bootstrap.sh                  # First-time setup: checks prerequisites, copies .env.example
│       ├── seed.sh                       # Seeds reference data (roles, market channels) per 04_DATA_MODEL_AND_OWNERSHIP.md §4.5
│       ├── health-check.sh               # Curls /actuator/health, reports ✅/❌
│       └── smoke-test.sh                 # Post-deploy critical-path check (login -> browse -> checkout)
│       # NOTE: no infra/k8s/ or infra/docker/[service].Dockerfile — no orchestration target is decided (source explicitly leaves this open); one Dockerfile lives at backend/app/Dockerfile
│
├── .github/workflows/
│   ├── ci.yml                            # lint -> type-check -> test (backend+frontend) -> build -> security-scan, on every PR
│   ├── cd.yml                            # Build+push Docker image to GHCR on merge to main; deploy step intentionally left as a placeholder (no provider chosen)
│   └── security-scan.yml                 # Weekly OWASP dependency-check + npm audit
│
├── docs/
│   ├── architecture/                     # The 17 planning documents + MVP.md, CODEX_MEMORY.md, DOCUMENT_INDEX.md (copied verbatim)
│   ├── openapi/                          # Placeholder tree per 05_API_CONTRACTS.md §6 (populate as endpoints are implemented)
│   └── onboarding.md                     # Prerequisites, zero-to-running steps, test commands, port table, reading order
│
└── storage/
    ├── README.md                          # Documents the uploads/ layout (source §12); actual runtime uploads are gitignored
    └── uploads/.gitkeep                   # Placeholder so the directory exists; real content never committed
```

## Why There Is No Root-Level `pom.xml`

The scaffold brief's template put `pom.xml` at the true repository root, alongside `frontend/`. That shape fits a repo where the Java project *is* the root. Here, `15_REPOSITORY_STRUCTURE.md` (already approved) puts the Maven project inside `backend/`, as a sibling to `frontend/`, `database/`, `docs/`, and `storage/` — consistent with the source workflow's own explicit repository tree (§35). Moving the Maven reactor to `backend/pom.xml` avoids contradicting that already-approved structure while keeping every other requested root-level file (`.gitignore`, `.editorconfig`, `README.md`, `docker-compose.yml`, `Makefile`, `.env.example`) genuinely at the repository root, where tooling expects to find them.

> ⚠️ Open Question: if the true intent is a Java-rooted monorepo (pom.xml at the literal top level, frontend/ as a subdirectory of a Maven-centric layout), that is a one-line change to move `backend/pom.xml` up — flag if this matters before development starts.

## Naming Conventions (unchanged from `15_REPOSITORY_STRUCTURE.md` §3)

Java packages: `com.artisanplatform.<module>`, lowercase. Migration files: `V0NN__snake_case_description.sql`. Branches: `feature/<name>`, `fix/<name>`. API paths: `/api/v1/<plural-noun>`. Commits: logical/grouped, imperative mood.

## What Was Deliberately Omitted (and why)

| Requested in the brief | Omitted because |
|---|---|
| Per-service `Dockerfile`, K8s `Deployment`/`Service`/`HPA` per domain | No microservices decomposition or Kubernetes target exists in the approved architecture (`02_ARCHITECTURE_OVERVIEW.md` §3, §4) |
| RabbitMQ topology, `messaging/producer`+`consumer` packages | `07_QUEUE_AND_CACHE_DESIGN.md` Part A: no broker for MVP |
| Redis use-cases, cache config | `07_QUEUE_AND_CACHE_DESIGN.md` Part B: no dedicated cache for MVP |
| Vault dev-mode service, secret mount structure | `08_SECURITY_AND_VAULT.md` Part B: environment variables for MVP |
| React + Vite web app, nginx nginx.conf, `vite.config.ts` | `13_FRONTEND_DASHBOARD_PLAN.md` / source §5, §27: React Native + Expo mobile app is the frontend |
| `fulfillment`/`experience` modules and migrations | PROPOSED, pending team approval (source §23/§24) — scaffolding them now would misrepresent them as approved |
