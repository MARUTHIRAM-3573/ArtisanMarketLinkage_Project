# 17 — Coding Backlog

Tasks are ordered by implementation sequence: infrastructure → core services → integrations → frontend → testing, matching `14_IMPLEMENTATION_ROADMAP.md`'s phases. Tasks marked **[CRITICAL PATH]** block the overall MVP timeline if delayed. Fulfillment (FUL-*) and Experience (EXP-*) tasks are marked **(PROPOSED)** and depend on the team approval gate described in `01_PRODUCT_SCOPE.md` §4.12–4.13.

---

## Infrastructure

**[INFRA-001] Scaffold monorepo structure**
- **Service:** platform-wide
- **Type:** infra
- **Size:** S
- **Depends On:** none
- **Blocks:** INFRA-002, INFRA-003, AUTH-001
- **Description:** Create the repository tree exactly as specified in `15_REPOSITORY_STRUCTURE.md` §2 (`backend/`, `frontend/`, `database/migrations/`, `docs/`, `storage/`, `.github/workflows/`), plus root `.gitignore`, `README.md`, `CODEOWNERS`.
- **Acceptance Criteria:**
  - [ ] All top-level directories exist per the annotated tree.
  - [ ] `.gitignore` excludes `uploads/`, `.env`, and any secret files.
  - [ ] `CODEOWNERS` matches `03_SERVICE_BOUNDARIES.md` §6.

**[INFRA-002] Provision PostgreSQL and initial Flyway migrations [CRITICAL PATH]**
- **Service:** database
- **Type:** migration
- **Size:** M
- **Depends On:** INFRA-001
- **Blocks:** AUTH-001, SELLER-001, CATALOG-001, MEDIA-001, INV-001, AI-001, PRICE-001, MARKET-001, B2B-001, COMM-001, PAY-001
- **Description:** Create database `artisan_marketplace`; author `V001__create_identity.sql` through `V011__create_payment.sql` per `04_DATA_MODEL_AND_OWNERSHIP.md` §2.1–2.11, including all tables, FKs, indexes, and constraints.
- **Acceptance Criteria:**
  - [ ] All 11 approved-schema migrations apply cleanly to an empty database.
  - [ ] Every FK, UNIQUE, and CHECK constraint listed in `04_DATA_MODEL_AND_OWNERSHIP.md` is present.
  - [ ] Migration files follow the `V0NN__snake_case.sql` naming convention.

**[INFRA-003] Spring Boot project bootstrap with module package skeleton**
- **Service:** backend
- **Type:** infra
- **Size:** S
- **Depends On:** INFRA-001
- **Blocks:** AUTH-001
- **Description:** Initialize the Maven Spring Boot project with the package skeleton from `15_REPOSITORY_STRUCTURE.md` §2 (`auth/`, `seller/`, `catalog/`, `media/`, `inventory/`, `ai/`, `pricing/`, `market/`, `b2b/`, `commerce/`, `payment/`, `common/`), Spring Security dependency, Flyway dependency, PostgreSQL driver.
- **Acceptance Criteria:**
  - [ ] `mvn package` succeeds on an empty skeleton.
  - [ ] Application connects to PostgreSQL and Flyway auto-runs migrations on startup in the `local` profile.

**[INFRA-004] Docker Compose local stack**
- **Service:** platform-wide
- **Type:** infra
- **Size:** S
- **Depends On:** INFRA-002, INFRA-003
- **Blocks:** none
- **Description:** Author `docker-compose.yml` exactly as specified in `10_CI_CD_AND_ENVIRONMENTS.md` §B.5, including the `uploads/` bind mount.
- **Acceptance Criteria:**
  - [ ] `docker compose up` brings up PostgreSQL and the backend successfully.
  - [ ] Uploaded files persist across container restarts.

**[INFRA-005] CI pipeline skeleton (GitHub Actions) [CRITICAL PATH]**
- **Service:** platform-wide
- **Type:** infra
- **Size:** M
- **Depends On:** INFRA-003
- **Blocks:** none (gates all future PRs)
- **Description:** Implement `.github/workflows/ci.yml` per `10_CI_CD_AND_ENVIRONMENTS.md` §A.6 (lint, test, build for backend and frontend).
- **Acceptance Criteria:**
  - [ ] CI runs on every PR to `main` and blocks merge on failure.
  - [ ] Backend job runs Testcontainers-based integration tests against ephemeral PostgreSQL.

**[INFRA-006] Common module: shared DTO/error/audit conventions**
- **Service:** backend (common)
- **Type:** feature
- **Size:** S
- **Depends On:** INFRA-003
- **Blocks:** AUTH-001, SELLER-001, CATALOG-001
- **Description:** Implement the shared `common/` package: base entity with `created_at`/`updated_at`, standard error response envelope, standard paged-response wrapper.
- **Acceptance Criteria:**
  - [ ] Every entity class extends the shared audit base.
  - [ ] Every controller returns errors in the shared envelope shape.

---

## Core Identity & Catalog (Phase 1)

**[AUTH-001] User registration and login [CRITICAL PATH]**
- **Service:** auth (identity)
- **Type:** feature
- **Size:** M
- **Depends On:** INFRA-002, INFRA-003, INFRA-006
- **Blocks:** SELLER-001, COMM-004, B2B-001
- **Description:** Implement `identity.users`/`roles`/`user_roles` entities/repositories; `POST /api/v1/auth/register`, `POST /api/v1/auth/login` per `05_API_CONTRACTS.md` §2.1; password hashing (bcrypt/argon2-class).
- **Acceptance Criteria:**
  - [ ] Passwords are never stored or logged in plaintext.
  - [ ] Duplicate email registration returns 409.
  - [ ] Valid login returns a bearer token; invalid credentials return 401.

**[AUTH-002] JWT issuance, validation, and refresh**
- **Service:** auth
- **Type:** feature
- **Size:** M
- **Depends On:** AUTH-001
- **Blocks:** every module requiring Bearer auth
- **Description:** Implement JWT signing/validation filter, `/api/v1/auth/refresh`, per `08_SECURITY_AND_VAULT.md` Part C.1.
- **Acceptance Criteria:**
  - [ ] Expired/tampered tokens are rejected with 401.
  - [ ] Refresh token exchange issues a new valid access token.

**[AUTH-003] Address book management**
- **Service:** auth
- **Type:** feature
- **Size:** S
- **Depends On:** AUTH-001
- **Blocks:** COMM-004 (checkout needs a shipping address)
- **Description:** CRUD for `identity.addresses`, supporting HOME/WORK/BUSINESS/WAREHOUSE/OTHER types.
- **Acceptance Criteria:**
  - [ ] A user can create, list, update, and delete their own addresses.
  - [ ] Non-owner cannot access another user's addresses (403).

**[SELLER-001] Seller and artisan profile creation [CRITICAL PATH]**
- **Service:** seller
- **Type:** feature
- **Size:** M
- **Depends On:** AUTH-001
- **Blocks:** CATALOG-001, B2B-001
- **Description:** Implement `seller.sellers`/`artisan_profiles`; seller registration endpoint supporting all five seller types.
- **Acceptance Criteria:**
  - [ ] A user with role ARTISAN can create exactly one seller record.
  - [ ] Second seller-creation attempt for the same user returns 409.

**[CATALOG-001] Category hierarchy [CRITICAL PATH]**
- **Service:** catalog
- **Type:** feature
- **Size:** S
- **Depends On:** INFRA-002
- **Blocks:** CATALOG-002
- **Description:** Implement `catalog.categories` with self-referential `parent_category_id`; `GET /api/v1/categories` returning the tree.
- **Acceptance Criteria:**
  - [ ] A 3-level category hierarchy (matching the Handicrafts/Bamboo Crafts/Baskets example) can be created and retrieved correctly.
  - [ ] Cycle creation (a category becoming its own ancestor) is rejected.

**[CATALOG-002] Product, SKU, and attribute management [CRITICAL PATH]**
- **Service:** catalog
- **Type:** feature
- **Size:** L
- **Depends On:** SELLER-001, CATALOG-001
- **Blocks:** MEDIA-002, PRICE-001, INV-001, MARKET-001, AI-004
- **Description:** Implement `catalog.products`/`product_skus`/`product_attributes`; `POST/GET/PUT /api/v1/products`, `GET /api/v1/products/{id}` per source §30, plus SKU/attribute sub-resource endpoints.
- **Acceptance Criteria:**
  - [ ] A seller can create a product with multiple SKUs distinguished by attributes.
  - [ ] Non-owner edit attempt returns 403.
  - [ ] Product entity has no stock/media/pricing/AI fields (architectural constraint test).

**[MEDIA-001] Media upload and storage abstraction [CRITICAL PATH]**
- **Service:** media
- **Type:** feature
- **Size:** M
- **Depends On:** INFRA-003
- **Blocks:** MEDIA-002, AI-001, AI-003
- **Description:** Implement `media.media_assets`; `MediaStorageService` interface with `LocalMediaStorage` implementation; `POST /api/v1/media/upload` with MIME/size validation per principle #32.5.
- **Acceptance Criteria:**
  - [ ] Disallowed MIME types rejected with 400; oversized files rejected with 413.
  - [ ] Uploaded file is stored under a generated UUID path, never a client-supplied path.
  - [ ] No binary data is ever written to a PostgreSQL column.

**[MEDIA-002] Product-media association**
- **Service:** media
- **Type:** feature
- **Size:** S
- **Depends On:** CATALOG-002, MEDIA-001
- **Blocks:** AI-003
- **Description:** Implement `media.product_media`; `POST /api/v1/products/{id}/media` supporting all five purposes (PRODUCT_IMAGE, THUMBNAIL, AI_ENHANCED, MARKETPLACE_IMAGE, OTHER).
- **Acceptance Criteria:**
  - [ ] Multiple media assets can be attached to one product with distinct purposes and display order.

**[INV-001] Inventory tracking and movement ledger [CRITICAL PATH]**
- **Service:** inventory
- **Type:** feature
- **Size:** M
- **Depends On:** CATALOG-002
- **Blocks:** COMM-002, B2B-003
- **Description:** Implement `inventory.inventories`/`inventory_movements`; movement-recording logic for all seven movement types with row-level locking to prevent oversell races (per `12_FAILURE_RESILIENCE_PLAN.md` §1).
- **Acceptance Criteria:**
  - [ ] Every stock change produces a movement record; `available_quantity` always equals the net of all movements.
  - [ ] Concurrent reservation attempts for the last unit of stock never both succeed (race condition test, per `09_TESTING_STRATEGY.md` §3.5).

---

## AI Digitization Pipeline (Phase 2)

**[AI-001] AI job orchestration framework [CRITICAL PATH]**
- **Service:** ai
- **Type:** feature
- **Size:** M
- **Depends On:** MEDIA-001
- **Blocks:** AI-002, AI-003, AI-004, AI-005
- **Description:** Implement `ai.ai_jobs`; generic job creation/status/polling infrastructure; provider-agnostic adapter interface per principle #14. `GET /api/v1/ai/jobs/{id}`.
- **Acceptance Criteria:**
  - [ ] A job can be created, transition through QUEUED/RUNNING/SUCCEEDED/FAILED, and be polled.
  - [ ] Adapter interface has no AI-vendor-specific types leaking into the `ai` module's public API.

**[AI-002] Voice input, speech-to-text, and translation**
- **Service:** ai
- **Type:** integration
- **Size:** L
- **Depends On:** AI-001
- **Blocks:** AI-004
- **Description:** Implement `ai.voice_inputs`/`speech_transcriptions`/`translations`; voice upload endpoint; STT and translation adapter calls per `06_COMMUNICATION_WORKFLOWS.md` §2.1.
- **Acceptance Criteria:**
  - [ ] A voice recording produces a transcription and a translation, both persisted and retrievable.
  - [ ] Provider timeout marks the job FAILED without corrupting partial data.

**[AI-003] AI image enhancement**
- **Service:** ai
- **Type:** integration
- **Size:** M
- **Depends On:** AI-001, MEDIA-002
- **Blocks:** none
- **Description:** Implement `ai.image_processing_results`; `POST /api/v1/ai/image/enhance` invoking background removal / lighting enhancement / e-commerce formatting; store output as a new media asset with purpose `AI_ENHANCED`.
- **Acceptance Criteria:**
  - [ ] Enhanced image is stored as a new, distinct media asset (never overwrites the original).
  - [ ] `image_processing_results.operations_applied` records which operations ran.

**[AI-004] AI catalog generation with human approval gate [CRITICAL PATH]**
- **Service:** ai
- **Type:** feature
- **Size:** L
- **Depends On:** AI-002, CATALOG-002
- **Blocks:** none
- **Description:** Implement `ai.catalog_generations`; `POST /api/v1/ai/catalog/generate`, `POST /ai/catalog/generations/{id}/approve` per `06_COMMUNICATION_WORKFLOWS.md` §2.1. Approval endpoint is the **only** code path permitted to create a `catalog.products` row from AI-originated data.
- **Acceptance Criteria:**
  - [ ] No `catalog.products` row is ever created without an explicit approval call (architectural constraint test, principle #9).
  - [ ] Artisan-edited fields at approval time are reflected in the created product, not the raw AI output.

**[PRICE-001] Cost records, market prices, and SKU prices [CRITICAL PATH]**
- **Service:** pricing
- **Type:** feature
- **Size:** M
- **Depends On:** CATALOG-002
- **Blocks:** AI-005, COMM-002
- **Description:** Implement `pricing.cost_records`/`market_prices`/`sku_prices` with validity-period support for SELLING/MRP/WHOLESALE price types.
- **Acceptance Criteria:**
  - [ ] Multiple historical prices for one SKU can coexist with only one active (open-ended `valid_to`) price per type.

**[AI-005] AI price recommendation with seller approval gate [CRITICAL PATH]**
- **Service:** ai
- **Type:** feature
- **Size:** M
- **Depends On:** AI-001, PRICE-001
- **Blocks:** none
- **Description:** Implement `ai.price_recommendations`; `POST /api/v1/ai/pricing/recommend`, `POST /ai/pricing/recommendations/{id}/accept` per `06_COMMUNICATION_WORKFLOWS.md` §2.3.
- **Acceptance Criteria:**
  - [ ] No `pricing.sku_prices` row is ever created from a recommendation without an explicit accept call (architectural constraint test).
  - [ ] Seller can override the recommended amount at accept time.

---

## Commerce Core (Phase 3)

**[MARKET-001] Market channels and listings [CRITICAL PATH]**
- **Service:** market
- **Type:** feature
- **Size:** M
- **Depends On:** CATALOG-002
- **Blocks:** COMM-001, B2B-002, MARKET-002
- **Description:** Implement `market.market_channels` (seeded B2C/B2B/GOVERNMENT), `market.market_listings`; listing CRUD supporting a product listed on multiple channels simultaneously.
- **Acceptance Criteria:**
  - [ ] One product can be listed on all three channels at once, and delisted from one without affecting the others.
  - [ ] Duplicate listing for the same product+channel returns 409.

**[MARKET-002] External marketplace integration scaffolding (deferred beyond MVP per `01_PRODUCT_SCOPE.md` §8.2)**
- **Service:** market
- **Type:** feature
- **Size:** S
- **Depends On:** MARKET-001
- **Blocks:** none
- **Description:** Implement `market.external_marketplaces`/`external_listings` tables and interfaces only (no real MANUAL/API/FILE integration built for MVP).
- **Acceptance Criteria:**
  - [ ] Tables exist and are isolated from core commerce logic (no FK from `commerce.orders` to any external-marketplace table).

**[COMM-001] Cart and cart items**
- **Service:** commerce
- **Type:** feature
- **Size:** S
- **Depends On:** MARKET-001, INV-001
- **Blocks:** COMM-002
- **Description:** Implement `commerce.carts`/`cart_items`; `GET/POST /api/v1/cart`, `DELETE /cart/items/{itemId}`.
- **Acceptance Criteria:**
  - [ ] Adding a SKU with insufficient stock returns 409.

**[COMM-002] Checkout and order creation [CRITICAL PATH]**
- **Service:** commerce
- **Type:** feature
- **Size:** L
- **Depends On:** COMM-001, AUTH-003, PRICE-001
- **Blocks:** PAY-001, B2B-004
- **Description:** Implement `commerce.orders`/`order_items`/`order_status_history`; `POST /api/v1/checkout` within a single DB transaction covering stock reservation + order creation + item snapshotting, per `06_COMMUNICATION_WORKFLOWS.md` §2.4.
- **Acceptance Criteria:**
  - [ ] Checkout with stock changed since cart-add returns 409 with no partial order created.
  - [ ] Order items snapshot product/SKU display data at time of order.
  - [ ] `source_type=B2C` set correctly.

**[COMM-003] Order retrieval and status history**
- **Service:** commerce
- **Type:** feature
- **Size:** S
- **Depends On:** COMM-002
- **Blocks:** PAY-002, FUL-001 (PROPOSED)
- **Description:** `GET /api/v1/orders`, `GET /api/v1/orders/{id}` with full status history; append-only status transition writer used by payment/fulfillment modules.
- **Acceptance Criteria:**
  - [ ] Non-party user cannot view another user's order (403).
  - [ ] Every status change produces an `order_status_history` row.

**[PAY-001] Payment initiation via mock gateway [CRITICAL PATH]**
- **Service:** payment
- **Type:** integration
- **Size:** M
- **Depends On:** COMM-002
- **Blocks:** PAY-002
- **Description:** Implement `payment.payments`/`payment_transactions`; mock payment gateway adapter behind a provider-agnostic interface (principle #14); `POST /orders/{id}/payments`.
- **Acceptance Criteria:**
  - [ ] SUCCESS outcome confirms the order and converts reserved stock to a SALE movement.
  - [ ] FAILED outcome leaves the order unconfirmed and releases reserved stock.
  - [ ] No card number, CVV, or full payment credential field exists anywhere in the payment DTOs/entities (architectural constraint test, principle #22/#40).

**[PAY-002] Invoicing and seller settlement**
- **Service:** payment
- **Type:** feature
- **Size:** M
- **Depends On:** PAY-001
- **Blocks:** none
- **Description:** Implement `payment.invoices`/`seller_settlements`; invoice generation on payment success; commission computation in the service layer (never a DB rule, per principle #15).
- **Acceptance Criteria:**
  - [ ] Settlement math matches the source's example pattern (gross − commission = net).
  - [ ] Invoice number is unique and generated exactly once per successful payment.

**[PAY-003] Refunds**
- **Service:** payment
- **Type:** feature
- **Size:** S
- **Depends On:** PAY-001
- **Blocks:** none
- **Description:** Implement `payment.refunds`; `POST /payments/{id}/refunds` restricted to ADMIN or the owning seller.
- **Acceptance Criteria:**
  - [ ] Refund amount cannot exceed the original payment amount.
  - [ ] Double-refund attempt beyond the paid amount is rejected.

---

## B2B and Government Channel (Phase 4)

**[B2B-001] B2B buyer registration**
- **Service:** b2b
- **Type:** feature
- **Size:** S
- **Depends On:** AUTH-001
- **Blocks:** B2B-002
- **Description:** Implement `b2b.b2b_buyers`; `POST /api/v1/b2b/buyers`.
- **Acceptance Criteria:**
  - [ ] A user with role B2B_BUYER can register exactly one buyer organization record.

**[B2B-002] Inquiry and quotation flow [CRITICAL PATH]**
- **Service:** b2b
- **Type:** feature
- **Size:** L
- **Depends On:** B2B-001, MARKET-001
- **Blocks:** B2B-003
- **Description:** Implement `b2b.b2b_inquiries`/`b2b_quotations`; inquiry submission, quotation response, per `06_COMMUNICATION_WORKFLOWS.md` §2.5.
- **Acceptance Criteria:**
  - [ ] Only the target seller can respond to an inquiry with a quotation.
  - [ ] Expired quotation (`validity_date` passed) cannot be accepted.

**[B2B-003] Quotation acceptance, purchase order, and inventory reservation [CRITICAL PATH]**
- **Service:** b2b
- **Type:** feature
- **Size:** M
- **Depends On:** B2B-002, INV-001
- **Blocks:** B2B-004
- **Description:** Implement `b2b.purchase_orders`; `POST /b2b/quotations/{id}/accept` triggering bulk stock reservation.
- **Acceptance Criteria:**
  - [ ] Accepting an already-accepted quotation returns 409.
  - [ ] Reservation quantity matches the quotation quantity exactly.

**[B2B-004] Purchase order resolution into commerce order [CRITICAL PATH]**
- **Service:** b2b + commerce
- **Type:** integration
- **Size:** M
- **Depends On:** B2B-003, COMM-002
- **Blocks:** none
- **Description:** Resolve an accepted purchase order into a `commerce.orders` row with `source_type=B2B`, `source_reference_id` set to the purchase order id; update `purchase_orders.order_id`.
- **Acceptance Criteria:**
  - [ ] Exactly one commerce order is created per accepted purchase order (no duplicates on retry — idempotency test per `06_COMMUNICATION_WORKFLOWS.md` §5).

**[MARKET-003] Government channel listing representation**
- **Service:** market
- **Type:** feature
- **Size:** S
- **Depends On:** MARKET-001
- **Blocks:** none
- **Description:** Ensure GOVERNMENT channel listing works identically to B2C/B2B via the existing `market_listings` mechanism; no separate table (per source §25 principle).
- **Acceptance Criteria:**
  - [ ] A product can be listed under GOVERNMENT independent of its B2C/B2B listing state.

> ⚠️ Open Question: No task exists for the "Procurement Opportunity" / "Institutional Purchase" interaction model beyond channel listing, because source §25 never defines these as concrete entities/endpoints (see open question in `06_COMMUNICATION_WORKFLOWS.md` §2.6) — blocks: 17_CODING_BACKLOG.md

---

## Fulfillment (PROPOSED — Phase 5, gated on team approval)

**[FUL-001] (PROPOSED) Fulfillment and shipment tracking**
- **Service:** fulfillment
- **Type:** feature
- **Size:** L
- **Depends On:** Team approval of fulfillment schema (source §23), COMM-003, PAY-001
- **Blocks:** FUL-002
- **Description:** Implement `fulfillment.fulfillments`/`shipments`/`shipment_items` per `04_DATA_MODEL_AND_OWNERSHIP.md` §2.12; endpoints per `05_API_CONTRACTS.md` §2.12.
- **Acceptance Criteria:**
  - [ ] Not started until team approval is recorded (principle #17 gate).
  - [ ] Once built: a paid order can be moved into a fulfillment and shipment record.

**[FUL-002] (PROPOSED) Delivery events and order status integration**
- **Service:** fulfillment
- **Type:** feature
- **Size:** M
- **Depends On:** FUL-001
- **Blocks:** EXP-001 (PROPOSED)
- **Description:** Implement `fulfillment.delivery_events`; feed SHIPPED/DELIVERED transitions into `commerce.order_status_history`.
- **Acceptance Criteria:**
  - [ ] Order status reflects the latest delivery event accurately.

---

## Experience (PROPOSED — post-MVP, gated on team approval)

**[EXP-001] (PROPOSED) Reviews and ratings**
- **Service:** experience
- **Type:** feature
- **Size:** M
- **Depends On:** Team approval of experience schema (source §24), FUL-002 (or COMM-003 if fulfillment remains unapproved but DELIVERED status is reachable another way)
- **Blocks:** none
- **Description:** Implement `experience.reviews`/`ratings` per `04_DATA_MODEL_AND_OWNERSHIP.md` §2.13; gate submission on the reviewer having a DELIVERED order item for the product.
- **Acceptance Criteria:**
  - [ ] Not started until team approval is recorded.
  - [ ] Once built: a customer without a DELIVERED order item cannot submit a review (403).

---

## Frontend (parallel track, Phase 6)

**[FE-001] Navigation shell and auth flow [CRITICAL PATH]**
- **Service:** frontend
- **Type:** feature
- **Size:** M
- **Depends On:** AUTH-002
- **Blocks:** FE-002 through FE-010
- **Description:** Implement role-aware navigation shell, login/register screens, secure token storage, refresh-on-401 interceptor per `13_FRONTEND_DASHBOARD_PLAN.md` §6.
- **Acceptance Criteria:**
  - [ ] App redirects to login on expired/invalid token.
  - [ ] Navigation tabs differ correctly by the authenticated user's role(s).

**[FE-002] Marketplace browse and product detail**
- **Service:** frontend
- **Type:** feature
- **Size:** M
- **Depends On:** FE-001, CATALOG-002, MARKET-001
- **Blocks:** FE-005
- **Description:** Implement Home/Browse/Category/Product Detail screens.
- **Acceptance Criteria:**
  - [ ] Category navigation and product detail render correctly against the live API.

**[FE-003] Seller product management + voice/image/pricing AI review screens [CRITICAL PATH]**
- **Service:** frontend
- **Type:** feature
- **Size:** L
- **Depends On:** FE-001, AI-004, AI-005, AI-003
- **Blocks:** none
- **Description:** Implement Product Management, Voice Capture, AI Catalog Review, Image Studio, AI Image Review, Pricing Review screens with job-status polling (`AIJobStatusIndicator` component).
- **Acceptance Criteria:**
  - [ ] Artisan can complete the full voice→catalog and image-enhancement flows from the app.
  - [ ] Polling stops correctly on terminal job status (SUCCEEDED/FAILED).

**[FE-004] Inventory management screen**
- **Service:** frontend
- **Type:** feature
- **Size:** S
- **Depends On:** FE-001, INV-001
- **Blocks:** none
- **Description:** Seller-facing inventory view and adjustment screen.
- **Acceptance Criteria:**
  - [ ] Stock levels and movement history display correctly per SKU.

**[FE-005] Cart, checkout, and payment**
- **Service:** frontend
- **Type:** feature
- **Size:** M
- **Depends On:** FE-002, COMM-002, PAY-001
- **Blocks:** FE-006
- **Description:** Implement Cart, Checkout, Payment screens.
- **Acceptance Criteria:**
  - [ ] Stock-conflict errors during checkout are surfaced clearly to the user.

**[FE-006] Order tracking**
- **Service:** frontend
- **Type:** feature
- **Size:** S
- **Depends On:** FE-005, COMM-003
- **Blocks:** none
- **Description:** Order list and detail screens with status history timeline.
- **Acceptance Criteria:**
  - [ ] Status history renders in chronological order.

**[FE-007] B2B inquiry and quotation screens**
- **Service:** frontend
- **Type:** feature
- **Size:** M
- **Depends On:** FE-001, B2B-002
- **Blocks:** none
- **Description:** Buyer-side inquiry submission/list, seller-side incoming inquiry/quotation response.
- **Acceptance Criteria:**
  - [ ] Both buyer and seller sides of the negotiation render correctly with role-appropriate actions.

**[FE-008] Android APK build pipeline [CRITICAL PATH]**
- **Service:** frontend
- **Type:** infra
- **Size:** S
- **Depends On:** FE-001
- **Blocks:** none
- **Description:** Configure Expo/EAS build for Android APK/AAB output per `10_CI_CD_AND_ENVIRONMENTS.md` §A.6 `build-android` job.
- **Acceptance Criteria:**
  - [ ] An installable APK can be produced and side-loaded onto a physical/emulated Android device.

---

## Testing (Phase 7)

**[TEST-001] Architectural constraint test suite [CRITICAL PATH]**
- **Service:** platform-wide
- **Type:** test
- **Size:** M
- **Depends On:** AI-004, AI-005, PAY-001, MEDIA-001
- **Blocks:** none
- **Description:** Automated tests asserting every hard constraint in `02_ARCHITECTURE_OVERVIEW.md` §7 (no AI-direct-write path, no card data field, no plaintext password field, no direct frontend-DB dependency in any shipped mobile config).
- **Acceptance Criteria:**
  - [ ] Suite fails the build if any hard constraint is violated.

**[TEST-002] Full test pyramid execution across all modules**
- **Service:** platform-wide
- **Type:** test
- **Size:** L
- **Depends On:** all module tasks above
- **Blocks:** none
- **Description:** Execute all per-domain scenarios in `09_TESTING_STRATEGY.md` §3 (happy path, edge case, failure mode per module).
- **Acceptance Criteria:**
  - [ ] Coverage and pass-rate thresholds in `09_TESTING_STRATEGY.md` §6 are met.

**[TEST-003] Performance baseline (k6)**
- **Service:** platform-wide
- **Type:** test
- **Size:** M
- **Depends On:** FE-008, TEST-002
- **Blocks:** none
- **Description:** Run load/stress/soak scenarios per `09_TESTING_STRATEGY.md` §7.4 against a staging deployment.
- **Acceptance Criteria:**
  - [ ] Baseline p95 latencies recorded for every endpoint class in `09_TESTING_STRATEGY.md` §7.2.

**[TEST-004] Security review and dependency scan**
- **Service:** platform-wide
- **Type:** test
- **Size:** M
- **Depends On:** TEST-002
- **Blocks:** none
- **Description:** Run SAST/dependency scan per `09_TESTING_STRATEGY.md` §8.2; manual review against the STRIDE table in `08_SECURITY_AND_VAULT.md` Part A.4.
- **Acceptance Criteria:**
  - [ ] No unresolved critical/high-severity findings before production deployment.
