# 16 — Architecture Decision Record Pack

---

**ADR-1: Modular Monolith Architectural Style**
- **Status:** Accepted
- **Context:** The platform needs domain separation across 13 business domains (identity, seller, catalog, media, inventory, ai, pricing, market, b2b, commerce, payment, fulfillment, experience) while shipping a working, demonstrable system within a hackathon timeline (source §43).
- **Decision:** Build one deployable Spring Boot application, internally decomposed into domain modules that each own one PostgreSQL schema, rather than a microservices architecture. See `02_ARCHITECTURE_OVERVIEW.md` §3 for full justification.
- **Consequences:** Enables fast iteration, simple local development, and transactional consistency across domains within one JVM. Constrains the team to enforce module boundaries by discipline/code review rather than network isolation; requires deliberate design (schema-per-module, service-interface-only cross-module calls) to remain extractable into microservices later.
- **Alternatives Considered:** Microservices (rejected — no requirement in source justifies the operational cost of a distributed system at this stage; would require a message broker and service discovery neither named nor implied by the workflow); single undifferentiated monolith with no internal module boundaries (rejected — violates principle #16/"existing structures should be extended, not duplicated" and would make future extraction impossible).

---

**ADR-2: PostgreSQL as the Primary Database Engine**
- **Status:** Accepted
- **Context:** The system needs a relational store supporting foreign keys, constraints, and multi-schema domain separation for 13 domains with strong referential integrity needs (orders, payments, inventory).
- **Decision:** Use PostgreSQL, single database `artisan_marketplace`, with one schema per domain (source §5, §6, §7 — explicit).
- **Consequences:** Enables ACID transactions across domains within a single connection (critical for checkout/payment/inventory correctness). Requires disciplined schema-boundary enforcement in application code since PostgreSQL itself would technically permit cross-schema joins.
- **Alternatives Considered:** NoSQL document store (rejected — the domain model is heavily relational with strict foreign-key relationships that a document store would need to reimplement in application code); separate databases per domain (rejected — source explicitly states "the goal is domain separation, not necessarily separate physical databases," §7).

---

**ADR-3: No Message Queue for MVP**
- **Status:** Accepted
- **Context:** Several candidate interactions (AI job processing, order-to-inventory-to-payment coordination) could be built as asynchronous, broker-mediated flows, but the source workflow never mentions a message broker anywhere.
- **Decision:** Do not introduce RabbitMQ or any message broker for the MVP. Use the `ai.ai_jobs` table plus in-process background execution to achieve the async-feeling client experience the workflow implies. See `07_QUEUE_AND_CACHE_DESIGN.md` Part A for the full reasoning and a PROPOSED future topology.
- **Consequences:** Zero additional infrastructure to operate for the hackathon. Constrains the system to single-instance-correct patterns (in-process thread pools) until/unless the team later approves adopting a broker for horizontal scaling.
- **Alternatives Considered:** RabbitMQ (deferred, not rejected outright — documented as a PROPOSED future design pending team approval); Kafka (rejected — heavier operational footprint than anything the workflow's scale implies).

---

**ADR-4: No Dedicated Cache Layer for MVP**
- **Status:** Accepted
- **Context:** Candidate cache use cases exist (catalog reads, rate limiting) but no cache technology is named in source, and the modular monolith runs as a single instance for MVP.
- **Decision:** Use Spring Boot's in-memory caching abstraction (Caffeine/`ConcurrentMapCacheManager`) and in-process rate limiting instead of Redis. See `07_QUEUE_AND_CACHE_DESIGN.md` Part B.
- **Consequences:** No new infrastructure to operate. Explicitly breaks (silently) if the backend is ever scaled to multiple instances before Redis is adopted — flagged as an open risk in `12_FAILURE_RESILIENCE_PLAN.md`.
- **Alternatives Considered:** Redis (deferred, documented as PROPOSED future design, not rejected); no caching at all, even in-memory (rejected — some read-heavy paths, like category tree lookups, benefit from trivial in-process caching at zero infrastructure cost).

---

**ADR-5: Spring Security with JWT Bearer Tokens for Authentication**
- **Status:** Proposed (token mechanism specifically; Spring Security itself is Accepted per source)
- **Context:** Source explicitly mandates Spring Security (§5) but does not specify the token/session mechanism. The frontend is a stateless mobile REST client (source §27).
- **Decision:** Use Spring Security to issue and validate JWT bearer tokens, with a short-lived access token and a longer-lived refresh token. See `08_SECURITY_AND_VAULT.md` Part C.
- **Consequences:** No server-side session store needed, simplifying horizontal scaling later. Requires careful token revocation handling (logout, compromised-token scenarios) since JWTs are not inherently revocable without an additional mechanism (e.g., a blacklist).
- **Alternatives Considered:** Server-side session cookies (rejected — awkward fit for a mobile app and contradicts the API-only, stateless frontend-backend contract implied by source §27); OAuth2/OIDC via a third-party identity provider (rejected — no such provider is named in source, and principle #2 disfavors adding one without a stated need).

---

**ADR-6: REST as the Sole API Style**
- **Status:** Accepted
- **Context:** The frontend needs to communicate with the backend for all thirteen domains' worth of functionality.
- **Decision:** Expose all backend functionality via REST APIs under `/api/v1` (source §5, §27, §30 — explicit, no alternative style ever mentioned).
- **Consequences:** Simple, well-understood tooling (standard HTTP clients, easy to test with curl/Postman); versioning is coarse-grained (whole-API version bump) rather than per-field, which is an accepted trade-off given no source requirement for finer-grained evolution.
- **Alternatives Considered:** GraphQL (rejected — not mentioned or implied anywhere in source, and would add a schema-stitching layer with no stated benefit); gRPC (rejected — poor fit for a mobile client needing simple HTTP/JSON, and unmentioned in source).

---

**ADR-7: React Native + TypeScript + Expo for the Mobile Frontend**
- **Status:** Accepted
- **Context:** The platform needs an Android-first mobile app deliverable as an installable APK for a hackathon demo, with iOS-compatible architecture where practical.
- **Decision:** Use React Native + TypeScript + Expo (source §5 — explicit, and explicitly *not* React web: "React (web) is not the primary frontend").
- **Consequences:** Expo's managed workflow simplifies APK/AAB builds without requiring a full native Android Studio project setup for most features; TypeScript enforces type safety across a 14-area screen surface (source §27). Constrains the team from using any native module that Expo's managed workflow doesn't support without ejecting.
- **Alternatives Considered:** Native Android (Kotlin/Java) (rejected — source explicitly names React Native, and native-only would sacrifice the "iOS-compatible architecture" goal); Flutter (rejected — not mentioned in source, and would introduce an entirely new language/toolchain with no stated justification).

---

**ADR-8: Minimal Spring-Boot-Native Observability Stack (Actuator + Micrometer + Prometheus + Grafana)**
- **Status:** Proposed
- **Context:** No observability tooling is named anywhere in source, but production operation of a system handling orders/payments needs at least basic metrics and structured logs.
- **Decision:** Use Spring Boot Actuator + Micrometer for metrics/tracing, SLF4J/Logback structured JSON logging, Prometheus for scraping, Grafana for dashboards. See `11_OBSERVABILITY_AND_INCIDENTS.md` Part A.
- **Consequences:** Zero new paid vendor; native to the existing Spring Boot stack. Lacks the polish of a commercial APM (e.g., no built-in anomaly detection) — an accepted trade-off for MVP scale.
- **Alternatives Considered:** A commercial APM (Datadog, New Relic) (rejected — no budget/vendor named in source, would violate principle #2); no observability at all (rejected — operating a payment-handling system with zero visibility is an unacceptable risk regardless of hackathon framing).

---

**ADR-9: Deployment Target Left as "Suitable Cloud/Server Environment" — Docker as the Portable Packaging Layer**
- **Status:** Accepted (non-decision is itself the decision, per source) / Proposed (Docker packaging specifically)
- **Context:** Source §28 explicitly states: "the exact provider is an implementation/deployment decision and is not hard-coded into the architecture."
- **Decision:** Do not commit to a specific cloud provider. Package the backend as a Docker image (see `10_CI_CD_AND_ENVIRONMENTS.md` §A.3) so it can run on any provider offering container hosting, keeping the provider decision genuinely open as source intends.
- **Consequences:** Maximum deployment flexibility; the team can choose based on cost/familiarity at demo time without any architecture rework. Docker itself is not named in source and is the reasoned minimal packaging choice to keep that flexibility real rather than theoretical.
- **Alternatives Considered:** Committing early to a specific PaaS/IaaS provider (rejected — directly contradicts source's explicit statement that the provider is not hard-coded); deploying the JAR directly on a bare VM with no containerization (rejected — makes environment parity across local/dev/staging/production, per `10_CI_CD_AND_ENVIRONMENTS.md` §B.2, much harder to guarantee).

---

**ADR-10: Environment Variables + Externalized Spring Config for Secrets (No Vault for MVP)**
- **Status:** Accepted
- **Context:** Source names no secrets-management product, but explicitly forbids committing database passwords/secrets to Git (principle #33.10).
- **Decision:** Use Spring Boot's externalized configuration (environment variables + per-profile `application.yml`) for secrets, never HashiCorp Vault or a cloud KMS, for the MVP. See `08_SECURITY_AND_VAULT.md` Part B.
- **Consequences:** Zero new infrastructure; satisfies the "never commit secrets" constraint. Lacks automated rotation, leasing, and per-access audit logging that Vault would provide — accepted trade-off, revisit if/when the team scales beyond hackathon operations.
- **Alternatives Considered:** HashiCorp Vault (deferred, documented as PROPOSED future design in `08_SECURITY_AND_VAULT.md` Part B.3-B.7, not rejected outright); cloud-provider-specific secret manager (rejected for now — would presuppose a cloud provider decision that ADR-9 explicitly keeps open).

---

**ADR-11: Row-Level Ownership as the Tenant Isolation Model (No Multi-Tenant SaaS Architecture)**
- **Status:** Accepted
- **Context:** The source describes a single shared marketplace database used by many sellers and buyers, with ownership expressed via foreign keys (`seller_id`, `user_id`), not a phrase like "tenant" or a request for per-organization isolation.
- **Decision:** Isolate data access by row-level ownership checks in the service layer (a seller/buyer can only touch their own records), not by per-tenant schemas, databases, or a tenant-routing layer. See `02_ARCHITECTURE_OVERVIEW.md` §5.
- **Consequences:** Simple, matches a marketplace model exactly. Would need to be revisited entirely (a materially different, unapproved architecture change) if the team's actual intent is white-labeled, per-organization marketplace instances.
- **Alternatives Considered:** Schema-per-tenant (rejected — no requirement for organizational isolation exists in source; would also conflict with the single-shared-marketplace reading of §7's "goal is domain separation, not necessarily separate physical databases"); database-per-tenant (rejected for the same reason, at even higher operational cost).

---

**ADR-12: GitHub Actions as the CI/CD Platform**
- **Status:** Proposed
- **Context:** Source explicitly uses GitHub for source control and PR-based collaboration (§28, §34) but names no CI/CD product.
- **Decision:** Use GitHub Actions for lint/type-check/test/build/security-scan/deploy pipelines. See `10_CI_CD_AND_ENVIRONMENTS.md` Part A.
- **Consequences:** No new vendor account needed beyond the GitHub repository the team already has. Ties CI/CD tooling to GitHub as the platform of record, which is already true for source control per source §28/§34.
- **Alternatives Considered:** GitLab CI, CircleCI, Jenkins (all rejected — none are named in source, and all would require either migrating source control off GitHub or adding a second vendor account with no stated justification).

---

**ADR-13: Flyway for Database Migrations**
- **Status:** Accepted
- **Context:** The database currently exists as manually created structure (source §37 status checklist) and needs to become version-controlled.
- **Decision:** Use Flyway, with versioned `V0NN__description.sql` files under `database/migrations/`, one initial migration per schema. Explicit in source §36.
- **Consequences:** Native, well-documented Spring Boot integration; forward-only migration model (no built-in free "undo," addressed via forward corrective migrations per `04_DATA_MODEL_AND_OWNERSHIP.md` §4.3).
- **Alternatives Considered:** Liquibase (rejected — source explicitly names Flyway, not Liquibase); hand-run manual SQL scripts with no tooling (rejected — source §33.9 explicitly requires migrations to be version-controlled).

---

**ADR-14: k6 for Performance Testing**
- **Status:** Proposed
- **Context:** No performance testing tool is named in source; the AI-heavy digitization flow and shared commerce order pipeline both need load validation before a public demo.
- **Decision:** Use k6 for load/stress/soak testing. See `09_TESTING_STRATEGY.md` §7.
- **Consequences:** JavaScript-based scripting aligns with the team's existing TypeScript frontend familiarity; free and CI-friendly. No built-in distributed load generation at very high scale — not a concern at this system's expected scale.
- **Alternatives Considered:** Apache JMeter (rejected — heavier XML-based configuration, less natural fit for a team already fluent in JS/TS); Locust (rejected — Python-based, would add a second scripting language ecosystem to the project with no source justification).

---

**ADR-15: Fulfillment and Experience Domains Remain PROPOSED, Not Implemented, Pending Team Approval**
- **Status:** Proposed
- **Context:** Source §23 and §24 explicitly mark both domains "STATUS: TO BE IMPLEMENTED" and state "exact implementation must be agreed before creation" — this is architecture principle #17 in direct application.
- **Decision:** Every document in this set that touches fulfillment or experience (schema, endpoints, modules, tests, backlog tasks) carries them as PROPOSED and flags an open question rather than presenting them as approved, current architecture.
- **Consequences:** Keeps the document set honest about what is and is not approved; requires the team to explicitly sign off before Phase 5 (`14_IMPLEMENTATION_ROADMAP.md`) begins. Slightly increases document verbosity (every fulfillment/experience mention repeats the caveat) in exchange for never silently smuggling an unapproved architecture change into the source of truth.
- **Alternatives Considered:** Silently treating the "likely entities"/"potential schema" language in source as already-approved (rejected — directly violates principle #17); omitting fulfillment/experience from the documents entirely until approved (rejected — the source's own MVP priority list includes "Fulfillment/order tracking" as item 15, so at least a proposed design is needed to keep the roadmap coherent).
