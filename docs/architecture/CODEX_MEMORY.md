# CODEX_MEMORY — Load This First, Every Session

## Project Name
Artisan Digital Commerce Platform (database: `artisan_marketplace`)

## Domain Summary
An AI-enabled digital commerce platform helping artisans with limited digital literacy digitize products (via voice + AI catalog generation + AI image enhancement + AI pricing recommendation) and sell through three channels: B2C (consumers), B2B (business buyers via inquiry/quotation/purchase-order), and Government/institutional procurement (market-channel representation). AI never writes directly to core business data — every AI result requires backend validation and human (artisan/seller) approval before it becomes a real product, price, or catalog entry.

## Full Service List (backend modules, single Spring Boot deployable — modular monolith, NOT microservices)
1. `auth` (schema: `identity`) — users, roles, user_roles, addresses
2. `seller` — sellers, artisan_profiles
3. `catalog` — categories, products, product_skus, product_attributes
4. `media` — media_assets, product_media
5. `inventory` — inventories, inventory_movements
6. `ai` — ai_jobs, voice_inputs, speech_transcriptions, translations, catalog_generations, image_processing_results, price_recommendations
7. `pricing` — cost_records, market_prices, sku_prices
8. `market` — market_channels, market_listings, external_marketplaces, external_listings
9. `b2b` — b2b_buyers, b2b_inquiries, b2b_quotations, purchase_orders
10. `commerce` — carts, cart_items, orders, order_items, order_status_history
11. `payment` — payments, payment_transactions, refunds, seller_settlements, invoices
12. `fulfillment` (PROPOSED, pending team approval — source explicitly marks "TO BE IMPLEMENTED")
13. `experience` (PROPOSED, pending team approval — source explicitly marks "TO BE IMPLEMENTED")

## Key Architectural Decisions (see `16_ADR_PACK.md` for full reasoning)
- Modular monolith, not microservices. One deployable, one database, 13 schemas.
- PostgreSQL (`artisan_marketplace`), UUID PKs, Flyway migrations (`V0NN__description.sql`).
- No message broker (no RabbitMQ) for MVP — async-feeling UX achieved via `ai.ai_jobs` polling.
- No dedicated cache (no Redis) for MVP — Spring in-memory caching only.
- No secrets vault (no HashiCorp Vault) for MVP — environment variables + Spring config profiles.
- Spring Security + JWT bearer tokens (PROPOSED mechanism, not literally named in source).
- REST only, base path `/api/v1`, no GraphQL/gRPC.
- React Native + TypeScript + Expo, Android-first, APK/AAB is the MVP distribution target (not Play Store).
- GitHub + GitHub Actions (PROPOSED CI platform), trunk-based with `feature/*`/`fix/*` branches into `main`.
- Docker packaging (PROPOSED) with deployment provider deliberately left open ("suitable cloud/server environment").
- Row-level ownership tenant isolation (seller_id/user_id FKs), not schema-per-tenant — this is a marketplace, not multi-tenant SaaS.

## Naming Conventions
- Schema/table: `snake_case`, schema-qualified (`catalog.products`).
- Java packages: lowercase, one per module (`auth`, `catalog`, ...), plus `common/` for shared DTOs/error handling/audit base entity.
- API paths: `/api/v1/<plural-noun>`.
- Migration files: `V0NN__snake_case_description.sql`.
- Branches: `feature/<name>`, `fix/<name>`.
- Commits: logical/grouped, never per-class.

## Critical Constraints (violating these is a defect, not a style choice)
1. AI must never write directly to `catalog.products` or `pricing.sku_prices` (or any core entity) — only a human-approval endpoint may do that, after reading an AI result.
2. Never store plaintext passwords, card numbers, CVV, or full payment credentials — hash passwords, store payment references only.
3. Media binaries never go into PostgreSQL — only metadata/storage references. Frontend never connects directly to PostgreSQL — REST APIs only.
4. Product and SKU are distinct concepts — never merge them or duplicate Product for B2C/B2B.
5. B2C, B2B, and Government all resolve into the same `commerce.orders` table (`source_type` distinguishes them) — never create parallel per-channel order tables.
6. Do not introduce a new technology (broker, cache, vault, orchestration platform) when the existing stack already satisfies the requirement — check `07_QUEUE_AND_CACHE_DESIGN.md` / `08_SECURITY_AND_VAULT.md` before proposing infrastructure additions.
7. `fulfillment` and `experience` schemas/modules are PROPOSED only — do not create them without an explicit, recorded team approval.
8. Any architecture change beyond what's in these documents must be marked **PROPOSED** and flagged, never silently implemented.

## Environment Setup Summary
Local: `docker compose up` (PostgreSQL + backend per `docker-compose.yml`), Flyway auto-migrates on startup, `npx expo start` for the mobile app, Android emulator uses `10.0.2.2:8080` as the backend base URL (not `localhost`). Full steps: `15_REPOSITORY_STRUCTURE.md` §6.2, `10_CI_CD_AND_ENVIRONMENTS.md` §B.5.

## Document Index (one-line descriptions)
See `DOCUMENT_INDEX.md` for the full table with dependencies and status.

## How to Use This File
Before proposing or implementing any change: (1) identify which module(s) it touches from the service list above; (2) check `03_SERVICE_BOUNDARIES.md` and `04_DATA_MODEL_AND_OWNERSHIP.md` for that module's existing boundary and schema; (3) check the Critical Constraints list above; (4) if the change requires something not already in this document set, mark it PROPOSED and flag it — do not implement silently. This mirrors the source workflow document's own instruction (§41, §42): read first, identify the domain, check existing design, propose before implementing anything not already covered.
