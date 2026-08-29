# 12 — Failure and Resilience Plan

## 1. Failure Mode Catalogue per Module

| Module | What can fail | How it fails | Impact |
|---|---|---|---|
| `auth` | DB unavailable; JWT signing key misconfigured | 500 on every request (auth gates everything) | Total platform outage — `auth` is the root dependency (`03_SERVICE_BOUNDARIES.md` §4) |
| `catalog` | DB unavailable; category cycle (bad `parent_category_id`) | 500 on reads/writes; infinite loop rendering category tree | Browse/listing outage; frontend crash if unguarded |
| `media` | Local disk full; storage path unreachable | Upload fails (500/507) | Artisans cannot digitize new products; existing media still servable if disk read-only but not full |
| `inventory` | Concurrent reservation race | Overselling (two checkouts both succeed for the last unit) | Customer-facing broken promise; requires manual reconciliation |
| `ai` | External AI provider down/slow/rate-limited | `ai_jobs.status=FAILED`, or requests queue up behind slow provider | Digitization pipeline stalls; does not affect already-listed products or existing orders |
| `pricing` | DB unavailable | 500 on price read/write | Sellers can't update prices; existing prices still readable by other modules via cache-fallback pattern if a cache exists |
| `market` | DB unavailable | 500 on listing changes | Channel visibility can't be changed, but existing listings remain visible |
| `b2b` | Quotation validity race (accept exactly at expiry) | Ambiguous accept outcome | Buyer/seller dispute risk |
| `commerce` | DB unavailable; inventory service in-process exception mid-checkout | Checkout fails entirely (transactional) | No partial orders — a Spring `@Transactional` boundary around checkout prevents a half-created order (order row without reserved stock, or vice versa) |
| `payment` | Mock/real gateway timeout or error | Payment stuck in PENDING or marked FAILED | Order not confirmed; stock reservation must be released (see idempotency rules in `06_COMMUNICATION_WORKFLOWS.md` §5) |
| `fulfillment` (PROPOSED) | Carrier/tracking data late or missing | Order stuck in PROCESSING/SHIPPED longer than expected | Customer-visible delay, no data corruption |
| `experience` (PROPOSED) | N/A (low blast radius) | Review submission fails | Cosmetic only |

## 2. Circuit Breaker Configuration per External Integration

No circuit breaker library is named in source. Resilience4j is the reasoned choice (Spring Boot-native integration, no new infrastructure, satisfies principle #2).

| Integration | Failure threshold (PROPOSED) | Open-state duration (PROPOSED) | Fallback |
|---|---|---|---|
| AI provider (STT/Translation/Catalog-Gen/Image/Pricing) | 50% failure rate over a rolling 10-call window | 30s before half-open retry | Mark `ai_jobs.status=FAILED` immediately without attempting the call; surface a "AI temporarily unavailable, try again shortly" response |
| Payment gateway | 50% failure rate over a rolling 10-call window | 30s before half-open retry | Reject the payment attempt with a clear retry-later error; never silently mark a payment SUCCESS |
| Future external marketplace integrations (`market.external_marketplaces`, API mode) | Same pattern once built | Same | Listing sync marked stale/pending, core commerce unaffected (explicit isolation goal, source §19) |

> ⚠️ Open Question: No circuit breaker thresholds are specified in source; Resilience4j and the numeric thresholds above are reasoned defaults — blocks: 12_FAILURE_RESILIENCE_PLAN.md

## 3. Retry Policies

| Operation | Backoff strategy (PROPOSED) | Max attempts | Idempotency |
|---|---|---|---|
| AI provider call | Exponential backoff (1s, 2s, 4s) | 3 | Safe — see `06_COMMUNICATION_WORKFLOWS.md` §5, AI generation is idempotent per source reference id |
| Payment gateway charge | No automatic retry | 1 (user must explicitly retry) | Charging is NOT auto-retried to avoid double-charge risk |
| Database transient connection error | Immediate retry, then exponential backoff (200ms, 400ms) | 3 | Safe — reads/writes inside a single transaction either fully commit or fully roll back |

## 4. Graceful Degradation Patterns per Domain

- **`ai` down:** Artisans can still create products manually (without voice/AI assistance) if the frontend exposes a manual entry path — the AI pipeline is a supporting capability, not the only path to a `catalog.products` row (consistent with principle #8, "AI services are supporting capabilities, not owners of core business data").
- **`media` storage degraded:** Existing product images continue to be servable (read path) even if new uploads (write path) are failing, as long as the storage read path and write path can fail independently (e.g., disk full blocks writes but not reads).
- **`market` external integrations down (future):** Core B2C/B2B/Government commerce is explicitly isolated from external marketplace sync issues (source §19 principle) — a stale external listing never blocks a local order.
- **`payment` gateway down:** Orders can still be placed and held in PENDING; nothing is lost, the customer simply cannot complete payment until the gateway recovers.

> ⚠️ Open Question: Whether a manual (non-AI) product-creation path is actually intended as a fallback is not explicitly stated in source — the AI flow is presented as the primary/showcased path (source §3, §15) but principle #8 implies AI is optional infrastructure, not a hard dependency — blocks: 12_FAILURE_RESILIENCE_PLAN.md, 13_FRONTEND_DASHBOARD_PLAN.md

## 5. Bulkhead and Timeout Configuration

- **Bulkhead (PROPOSED):** a dedicated, bounded thread pool for AI-provider-calling code, separate from the main HTTP request-handling thread pool, so a slow/hanging AI provider cannot starve the threads needed to serve ordinary catalog browse/checkout traffic.
- **Timeouts (PROPOSED, none specified in source):** AI provider calls — 30s; payment gateway calls — 15s; database queries — 5s (Spring's default connection pool timeout, tightened for query-level statements handling user-facing requests).

## 6. Chaos Engineering Baseline Scenarios

Not mentioned in source at all — reasoned as an appropriate but explicitly optional, post-MVP practice given hackathon time constraints (principle #43):

- Kill the backend process mid-checkout — verify no half-committed order/inventory state (transactional boundary test).
- Simulate AI provider total outage — verify the platform remains otherwise fully usable (browse, B2C purchase of already-listed products, B2B, payment).
- Simulate PostgreSQL connection exhaustion — verify graceful 503s rather than cascading crashes.

Tooling: none named in source; a simple scripted fault-injection harness (e.g., toggling a feature flag that forces the AI adapter to throw) is sufficient at this scale — no dedicated chaos engineering platform is justified for an MVP.

> ⚠️ Open Question: Chaos engineering is not mentioned anywhere in source; the scenarios above are a reasoned minimal baseline, explicitly marked optional/post-MVP — blocks: 12_FAILURE_RESILIENCE_PLAN.md

## 7. Disaster Recovery Objectives

Not specified in source. Reasoned defaults given the hackathon MVP framing (single environment, no HA requirement per `01_PRODUCT_SCOPE.md` §5.2):

| Service tier | RTO (PROPOSED) | RPO (PROPOSED) |
|---|---|---|
| PostgreSQL (all commerce/financial data) | 4 hours | 24 hours (daily backup) — tighten once real transaction volume exists |
| Media storage (local filesystem) | 4 hours | 24 hours, or continuous if migrated to S3 (future, source §5/§12) |
| Backend application | 30 minutes (redeploy from last known-good image, per `10_CI_CD_AND_ENVIRONMENTS.md` §A.5) | N/A (stateless) |

> ⚠️ Open Question: No RTO/RPO targets or backup cadence are specified in source — blocks: 12_FAILURE_RESILIENCE_PLAN.md

## 8. Recovery Procedures per Failure Class

| Failure class | Recovery procedure |
|---|---|
| Backend crash/outage | Redeploy last known-good image tag (`10_CI_CD_AND_ENVIRONMENTS.md` §A.5); investigate root cause via logs/traces (`11_OBSERVABILITY_AND_INCIDENTS.md`). |
| Bad Flyway migration deployed | Write and deploy a corrective forward migration (never edit the merged file); if data was corrupted, restore affected rows from the most recent backup. |
| Overselling detected (inventory race) | Manual reconciliation: cancel/refund the excess order(s) per business decision, log an `ADJUSTMENT` inventory movement to correct `available_quantity`. |
| Payment stuck in PENDING beyond a reasonable window | Reconcile against the gateway's own transaction record (mock gateway for MVP simplifies this); mark FAILED and release reserved stock if the gateway confirms no charge occurred. |
| Media storage disk full | Free space / expand volume; once recovered, retry failed uploads client-side (uploads are not silently retried server-side without user awareness). |
