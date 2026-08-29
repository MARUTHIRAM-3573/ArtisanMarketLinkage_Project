# MVP Scope

## 1. Strict MVP Scope

Matches source §43's own explicit MVP priority list, translated into minimum services, features, and infrastructure.

### 1.1 Minimum Services (Backend Modules)

`auth`, `seller`, `catalog`, `media`, `inventory`, `ai`, `pricing`, `market`, `b2b`, `commerce`, `payment` — 11 modules, all schemas already marked "Completed" in source §37. **`fulfillment` and `experience` are explicitly excluded from strict MVP** (see §2).

### 1.2 Minimum Features

1. Artisan onboarding (register, create seller + artisan profile).
2. Product digitization (manual product/SKU/attribute creation, working independently of AI per principle #8).
3. Voice → Speech-to-Text → Translation → AI catalog generation, with mandatory artisan approval gate.
4. AI image enhancement (background removal, lighting, e-commerce formatting).
5. AI pricing recommendation, with mandatory seller approval gate.
6. Product/SKU/inventory management with movement history.
7. B2C marketplace: browse, cart, checkout, order.
8. B2B: inquiry, quotation, purchase order, resolved into a commerce order.
9. Government/institutional market channel representation (listing-only, per the open question on procurement-opportunity mechanics in `06_COMMUNICATION_WORKFLOWS.md` §2.6).
10. Order lifecycle: PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED status tracking (status changes may be manually triggered/simulated if fulfillment automation is deferred — see §2).
11. Payment via mock gateway: payment, transaction, invoice, refund, seller settlement.

### 1.3 Minimum Infrastructure

- PostgreSQL (`artisan_marketplace`, 11 schemas) with Flyway migrations.
- Spring Boot backend (single deployable, modular monolith).
- React Native + Expo Android APK.
- Local filesystem media storage.
- GitHub Actions CI (lint/test/build).
- Docker Compose for local development.
- No message broker, no Redis, no Vault, no Kubernetes (see `07_QUEUE_AND_CACHE_DESIGN.md` and `08_SECURITY_AND_VAULT.md` for the reasoning — none of these are required to satisfy any MVP feature above).

## 2. Explicitly Deferred to Phase 2 and Beyond

| Deferred item | Reasoning |
|---|---|
| **Fulfillment domain** (real shipment/carrier tracking, `fulfillment.*` tables) | Source §23 explicitly marks this "STATUS: TO BE IMPLEMENTED" and requires team agreement before creation. Order status can still reach SHIPPED/DELIVERED via a manual/admin-triggered status update in the interim, satisfying the MVP priority list's "Fulfillment/order tracking" item at a reduced (non-automated) fidelity. |
| **Experience domain** (reviews, ratings, favorites/wishlist) | Source §24 explicitly marks this "STATUS: TO BE IMPLEMENTED" and is not present anywhere in the MVP priority list (source §43) at all. |
| **Real payment gateway** | Source §22 explicitly permits a mock gateway for the hackathon MVP. |
| **S3/object storage migration** | Source §5/§12 explicitly frames this as "future-compatible," not current. Local storage behind the `MediaStorageService` abstraction is sufficient and keeps the migration path open. |
| **External marketplace integrations** (MANUAL/API/FILE) | Source §19 frames this as a future integration surface; only the abstraction (tables/interfaces) needs to exist now, not a working integration. |
| **Google Play Store distribution** | Source §28 explicitly states this is a later step, "not required for the MVP unless time permits." Direct APK install is the MVP target. |
| **iOS build** | Source keeps the architecture iOS-compatible but does not target an actual iOS build for the hackathon. |
| **RabbitMQ, Redis, Vault, Kubernetes** | None are mentioned or implied anywhere in the source workflow; introducing them would violate principle #2 ("do not introduce new technology when existing technology already satisfies the requirement"). See `07_QUEUE_AND_CACHE_DESIGN.md` and `08_SECURITY_AND_VAULT.md` for the full reasoning and PROPOSED future designs. |
| **Formal admin moderation/verification tooling** | No admin functional requirements are specified in source beyond the existence of the ADMIN role (open question in `01_PRODUCT_SCOPE.md` §3.5); a minimal manual/DB-level admin capability is sufficient for MVP, a full admin UI is not. |
| **Rate limiting, circuit breakers, observability stack, chaos engineering** | All reasoned as PROPOSED hardening in `07`–`12`, appropriate for a demo-then-harden trajectory but not blocking a working, demonstrable MVP. |

## 3. MVP Definition of "Done"

The MVP is complete when every acceptance criterion in `01_PRODUCT_SCOPE.md` §7.1–7.8 (excluding §7.9 Fulfillment, which is explicitly PROPOSED) passes end to end on an installable Android APK talking to a publicly reachable backend, per the release strategy in `14_IMPLEMENTATION_ROADMAP.md` §7 ("Hackathon Demo" milestone).
