# Document Index

| # | Filename | Purpose | Depends On | Status |
|---|---|---|---|---|
| 01 | `01_PRODUCT_SCOPE.md` | Vision, personas, functional/non-functional requirements, MVP boundary | Source workflow document only | Complete |
| 02 | `02_ARCHITECTURE_OVERVIEW.md` | System context/component diagrams, modular-monolith style decision, tech stack, multi-tenancy | 01 | Complete |
| 03 | `03_SERVICE_BOUNDARIES.md` | Module catalogue, bounded contexts, dependency graph, CODEOWNERS | 02 | Complete |
| 04 | `04_DATA_MODEL_AND_OWNERSHIP.md` | Full ERD, per-entity schema, migration strategy, retention | 02, 03 | Complete |
| 05 | `05_API_CONTRACTS.md` | Endpoint catalogue, auth model, rate limiting, OpenAPI structure | 03, 04 | Complete |
| 06 | `06_COMMUNICATION_WORKFLOWS.md` | Sync/async matrix, sequence diagrams, error/retry/idempotency | 03, 04, 05 | Complete |
| 07 | `07_QUEUE_AND_CACHE_DESIGN.md` | MVP no-broker/no-cache decision + PROPOSED future RabbitMQ/Redis design | 02, 06 | Complete |
| 08 | `08_SECURITY_AND_VAULT.md` | Threat model, STRIDE, secrets approach, auth architecture | 02, 03, 04, 05 | Complete |
| 09 | `09_TESTING_STRATEGY.md` | Test pyramid, per-domain scenarios, CI gates, perf/security testing | 03, 04, 05, 06 | Complete |
| 10 | `10_CI_CD_AND_ENVIRONMENTS.md` | CI/CD pipeline, branch strategy, environments, local dev stack | 02, 03, 09 | Complete |
| 11 | `11_OBSERVABILITY_AND_INCIDENTS.md` | Logs/metrics/traces, incident classification and lifecycle | 03, 09, 10 | Complete |
| 12 | `12_FAILURE_RESILIENCE_PLAN.md` | Failure catalogue, circuit breakers, retries, RTO/RPO | 03, 06, 07 | Complete |
| 13 | `13_FRONTEND_DASHBOARD_PLAN.md` | Screen hierarchy per persona, state management, auth flow | 01, 05, 06, 08 | Complete |
| 14 | `14_IMPLEMENTATION_ROADMAP.md` | WBS, phases, risk register, DoD, release strategy | 01–13 (synthesizes all) | Complete |
| 15 | `15_REPOSITORY_STRUCTURE.md` | Directory tree, conventions, onboarding guide | 02, 03, 10 | Complete |
| 16 | `16_ADR_PACK.md` | 15 formal ADRs covering every major technology/architecture decision | 02–13 | Complete |
| 17 | `17_CODING_BACKLOG.md` | Full sequenced, agentic-ready task list across all modules | 01–16 (synthesizes all) | Complete |
| — | `MVP.md` | Strict MVP scope vs. deferred items, with reasoning | 01, 14 | Complete |
| — | `CODEX_MEMORY.md` | Compressed session-start context for an AI coding agent | 01–17 (synthesizes all) | Complete |
| — | `DOCUMENT_INDEX.md` | This file — single reference table for the whole set | All | Complete |

## Notes on "Status"

Every document above is **content-complete** per the global rule of no placeholders/TBDs. "Complete" does **not** mean every open question is resolved — it means every section has fully reasoned content, with unresolved workflow ambiguities explicitly flagged inline as `⚠️ Open Question` blocks rather than left blank. See the closing section of this response (outside this file set) for the consolidated list of open questions across all 20 documents.

Two domains — **Fulfillment** (`fulfillment` schema/module) and **Experience** (`experience` schema/module) — are carried throughout every document as **PROPOSED**, consistent with the source workflow document's own explicit "STATUS: TO BE IMPLEMENTED" / "must be agreed before creation" markers (source §23, §24) and architecture principle #17. No document in this set treats them as approved, current architecture.
