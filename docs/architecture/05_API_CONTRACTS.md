# 05 — API Contracts

## 1. API Design Principles and Versioning Strategy

- Base path: `/api/v1` (explicit, source §30). All endpoints are versioned by URL prefix; a breaking change to any contract requires a new `/api/v2` prefix rather than an in-place breaking change, since no alternative versioning scheme (header-based, content-negotiation) is mentioned in source.
- REST is the sole API style — no GraphQL or gRPC is mentioned or implied anywhere in the workflow (relevant to `16_ADR_PACK.md` ADR on API style).
- Naming and response conventions are consistent across domains (source §30: "API naming and response conventions should remain consistent across domains") — resource-oriented plural nouns (`/products`, `/orders`), standard HTTP verbs, standard status codes.
- DTOs are used at API boundaries, never raw JPA entities (source §29).

## 2. Full Endpoint Catalogue per Module

Endpoints marked **(explicit)** are stated verbatim in source §30. All others are **(implied)** — reasoned from the functional requirements in `01_PRODUCT_SCOPE.md` and marked as such; exact request/response field names are the author's best reasoning from the data model in `04_DATA_MODEL_AND_OWNERSHIP.md` and are not literal source text.

### 2.1 `auth` module

| Method | Path | Auth | Request | Response | Errors |
|---|---|---|---|---|---|
| POST | `/api/v1/auth/login` **(explicit)** | None | `{email, password}` | `{accessToken, user}` | 400 invalid body, 401 bad credentials |
| POST | `/api/v1/auth/register` (implied) | None | `{email, password, fullName, roles[]}` | `{userId}` | 400, 409 email exists |
| GET | `/api/v1/auth/me` (implied) | Bearer | — | `{id, email, fullName, roles[]}` | 401 |
| GET/POST/PUT/DELETE | `/api/v1/addresses` (implied) | Bearer | Address fields | Address DTO | 400, 401, 404 |

### 2.2 `seller` module

| Method | Path | Auth | Request | Response | Errors |
|---|---|---|---|---|---|
| POST | `/api/v1/sellers` (implied) | Bearer, role ARTISAN | `{sellerType, displayName}` | Seller DTO | 400, 401, 409 already a seller |
| GET | `/api/v1/sellers/{id}` (implied) | Bearer | — | Seller DTO + artisan profile | 401, 404 |
| PUT | `/api/v1/sellers/{id}/artisan-profile` (implied) | Bearer, owner-only | Profile fields | Profile DTO | 400, 401, 403, 404 |

### 2.3 `catalog` module

| Method | Path | Auth | Request | Response | Errors |
|---|---|---|---|---|---|
| GET | `/api/v1/products` **(explicit)** | None (public browse) | Query: category, channel, sellerId, page | Paged Product DTO list | 400 |
| POST | `/api/v1/products` **(explicit)** | Bearer, role ARTISAN, owner=seller | Product create DTO | Product DTO | 400, 401, 403 |
| GET | `/api/v1/products/{id}` **(explicit)** | None | — | Product DTO with SKUs, attributes, media | 404 |
| PUT | `/api/v1/products/{id}` **(explicit)** | Bearer, owner-only | Product update DTO | Product DTO | 400, 401, 403, 404 |
| GET/POST | `/api/v1/products/{id}/skus` (implied) | Mixed | SKU fields | SKU DTO | 400, 401, 403, 404 |
| GET/POST | `/api/v1/products/{id}/attributes` (implied) | Mixed | Attribute fields | Attribute DTO | 400, 401, 403, 404 |
| GET | `/api/v1/categories` (implied) | None | — | Category tree | — |

### 2.4 `media` module

| Method | Path | Auth | Request | Response | Errors |
|---|---|---|---|---|---|
| POST | `/api/v1/media/upload` **(explicit)** | Bearer | `multipart/form-data` file + metadata | Media asset DTO | 400 invalid MIME/size, 401, 413 too large |
| POST | `/api/v1/products/{id}/media` (implied) | Bearer, owner-only | `{mediaAssetId, purpose}` | Product-media DTO | 400, 401, 403, 404 |

### 2.5 `inventory` module

| Method | Path | Auth | Request | Response | Errors |
|---|---|---|---|---|---|
| GET | `/api/v1/skus/{skuId}/inventory` (implied) | Bearer, owner-only | — | Inventory DTO | 401, 403, 404 |
| POST | `/api/v1/skus/{skuId}/inventory/movements` (implied) | Bearer, owner-only | `{movementType, quantity, referenceType, referenceId}` | Movement DTO | 400, 401, 403, 404, 409 insufficient stock |

### 2.6 `ai` module

| Method | Path | Auth | Request | Response | Errors |
|---|---|---|---|---|---|
| POST | `/api/v1/ai/voice/upload` (implied) | Bearer, role ARTISAN | Voice media reference | `{voiceInputId, aiJobId}` | 400, 401 |
| POST | `/api/v1/ai/catalog/generate` **(explicit)** | Bearer, role ARTISAN | `{translationId}` or `{voiceInputId}` | `{catalogGenerationId, aiJobId, status}` | 400, 401, 422 upstream AI failure |
| GET | `/api/v1/ai/catalog/generations/{id}` (implied) | Bearer, owner-only | — | Catalog generation DTO | 401, 403, 404 |
| POST | `/api/v1/ai/catalog/generations/{id}/approve` (implied) | Bearer, owner-only | Optional edits | Created `Product` DTO | 400, 401, 403, 404, 409 already approved |
| POST | `/api/v1/ai/image/enhance` **(explicit)** | Bearer, owner-only | `{sourceMediaAssetId}` | `{imageProcessingResultId, aiJobId, status}` | 400, 401, 422 |
| POST | `/api/v1/ai/pricing/recommend` **(explicit)** | Bearer, owner-only | `{productId}` | `{priceRecommendationId, recommendedPrice, aiJobId}` | 400, 401, 422 |
| POST | `/api/v1/ai/pricing/recommendations/{id}/accept` (implied) | Bearer, owner-only | Optional override amount | Created/updated `SkuPrice` DTO | 400, 401, 403, 404 |
| GET | `/api/v1/ai/jobs/{id}` (implied) | Bearer, owner-only | — | AI job status DTO | 401, 403, 404 |

### 2.7 `pricing` module

| Method | Path | Auth | Request | Response | Errors |
|---|---|---|---|---|---|
| GET/POST | `/api/v1/products/{id}/cost-records` (implied) | Bearer, owner-only | Cost fields | Cost record DTO | 400, 401, 403, 404 |
| GET/POST | `/api/v1/skus/{skuId}/prices` (implied) | Mixed (read public, write owner-only) | Price fields | SKU price DTO | 400, 401, 403, 404 |

### 2.8 `market` module

| Method | Path | Auth | Request | Response | Errors |
|---|---|---|---|---|---|
| GET/POST | `/api/v1/products/{id}/listings` (implied) | Mixed | `{marketChannel}` | Listing DTO | 400, 401, 403, 404 |
| GET | `/api/v1/market/channels` (implied) | None | — | Channel list | — |

### 2.9 `b2b` module

| Method | Path | Auth | Request | Response | Errors |
|---|---|---|---|---|---|
| POST | `/api/v1/b2b/buyers` (implied) | Bearer, role B2B_BUYER | Org fields | B2B buyer DTO | 400, 401, 409 |
| POST | `/api/v1/b2b/inquiries` (implied) | Bearer, role B2B_BUYER | Inquiry fields | Inquiry DTO | 400, 401, 404 product not found |
| GET | `/api/v1/b2b/inquiries/{id}` (implied) | Bearer, buyer or seller party only | — | Inquiry DTO | 401, 403, 404 |
| POST | `/api/v1/b2b/inquiries/{id}/quotations` (implied) | Bearer, seller-only | Quotation fields | Quotation DTO | 400, 401, 403, 404 |
| POST | `/api/v1/b2b/quotations/{id}/accept` (implied) | Bearer, buyer-only | — | Purchase order DTO | 401, 403, 404, 409 expired |
| GET | `/api/v1/b2b/purchase-orders/{id}` (implied) | Bearer, buyer or seller party only | — | Purchase order DTO + resolved order (if any) | 401, 403, 404 |

### 2.10 `commerce` module

| Method | Path | Auth | Request | Response | Errors |
|---|---|---|---|---|---|
| GET/POST | `/api/v1/cart` (implied) | Bearer | `{productSkuId, quantity}` | Cart DTO | 400, 401, 409 insufficient stock |
| DELETE | `/api/v1/cart/items/{itemId}` (implied) | Bearer, owner-only | — | Cart DTO | 401, 403, 404 |
| POST | `/api/v1/checkout` (implied) | Bearer | `{shippingAddressId}` | Order DTO | 400, 401, 409 stock changed |
| GET | `/api/v1/orders` (implied) | Bearer | Query: status, sourceType | Paged order list | 401 |
| GET | `/api/v1/orders/{id}` (implied) | Bearer, party-only | — | Order DTO + items + status history | 401, 403, 404 |

### 2.11 `payment` module

| Method | Path | Auth | Request | Response | Errors |
|---|---|---|---|---|---|
| POST | `/api/v1/orders/{id}/payments` (implied) | Bearer, owner-only | `{paymentMethod}` (mock gateway) | Payment DTO | 400, 401, 403, 404, 402 gateway declined |
| GET | `/api/v1/payments/{id}` (implied) | Bearer, owner-only | — | Payment DTO + transactions | 401, 403, 404 |
| POST | `/api/v1/payments/{id}/refunds` (implied) | Bearer, role ADMIN or seller | `{amount, reason}` | Refund DTO | 400, 401, 403, 404, 409 already refunded |
| GET | `/api/v1/invoices/{id}` (implied) | Bearer, owner-only | — | Invoice DTO | 401, 403, 404 |

### 2.12 `fulfillment` module (PROPOSED)

| Method | Path | Auth | Request | Response | Errors |
|---|---|---|---|---|---|
| POST | `/api/v1/orders/{id}/fulfillment` (PROPOSED) | Bearer, seller-only | — | Fulfillment DTO | 400, 401, 403, 404 |
| POST | `/api/v1/fulfillments/{id}/shipments` (PROPOSED) | Bearer, seller-only | Shipment fields | Shipment DTO | 400, 401, 403, 404 |
| POST | `/api/v1/shipments/{id}/events` (PROPOSED) | Bearer, seller-only | `{eventType, notes}` | Delivery event DTO | 400, 401, 403, 404 |
| GET | `/api/v1/orders/{id}/tracking` (PROPOSED) | Bearer, owner-only | — | Shipment + events | 401, 403, 404 |

> ⚠️ Open Question: All fulfillment endpoints are PROPOSED and blocked on the schema being approved (source §23) — blocks: 05_API_CONTRACTS.md

### 2.13 `experience` module (PROPOSED)

| Method | Path | Auth | Request | Response | Errors |
|---|---|---|---|---|---|
| POST | `/api/v1/products/{id}/reviews` (PROPOSED) | Bearer, must have a DELIVERED order item for the product | `{reviewText}` | Review DTO | 400, 401, 403, 404, 409 already reviewed |
| POST | `/api/v1/products/{id}/ratings` (PROPOSED) | Bearer, same gate as reviews | `{score}` | Rating DTO | 400, 401, 403, 404 |
| GET | `/api/v1/products/{id}/reviews` (PROPOSED) | None | — | Paged review list + aggregate rating | 404 |

> ⚠️ Open Question: All experience endpoints are PROPOSED and blocked on the schema being approved (source §24) — blocks: 05_API_CONTRACTS.md

## 3. Authentication and Authorization Model per Endpoint Group

| Endpoint group | AuthN requirement | AuthZ rule |
|---|---|---|
| Public catalog browse (`GET /products`, `/categories`, `/products/{id}`, reviews read) | None | Public |
| Auth/registration | None (login/register are the entry point) | Public |
| Own-resource read/write (addresses, cart, orders, payments, seller profile, AI jobs) | Bearer token | Requester must be the resource owner (identity match on `user_id`/`seller_id`) or a party to the transaction (e.g., both sides of a B2B inquiry) |
| Seller-scoped write (products, SKUs, media, pricing, inventory, listings) | Bearer token | Requester must hold role ARTISAN (or COOPERATIVE/SHG/ARTISAN_GROUP/BUSINESS seller type) and own the target seller record |
| B2B-scoped write (inquiries, quotations, purchase orders) | Bearer token | Buyer-side actions require role B2B_BUYER and ownership of the `b2b_buyers` record; seller-side actions (quotations) require ownership of the target product's seller record |
| Admin-only (refund approval, moderation — reasoned per §3.5 of `01_PRODUCT_SCOPE.md`) | Bearer token | Requester must hold role ADMIN |

Full mechanism detail (token type, lifecycle, refresh) is specified in `08_SECURITY_AND_VAULT.md` Part C, since the source names Spring Security but not a specific token scheme.

## 4. Rate Limiting and Throttling Rules

The source workflow does not mention rate limiting, throttling, or API quotas anywhere. Given the AI endpoints proxy to external providers that likely have their own cost/quota constraints, a minimal reasoned default is proposed rather than left unspecified, since "no rate limiting" for an AI-cost-bearing endpoint is an operational risk:

- NFR (PROPOSED): AI-invoking endpoints (`/api/v1/ai/*`) are throttled per authenticated user (e.g., N requests per minute) to bound external AI provider cost exposure.
- NFR (PROPOSED): Authentication endpoints (`/api/v1/auth/login`) are throttled per IP/email to reduce brute-force risk.
- No throttling is applied to public catalog browse endpoints, since a marketplace's core discovery surface should stay maximally available.

> ⚠️ Open Question: No rate limiting or throttling policy, and no numeric thresholds, are specified anywhere in the workflow — the guidance above is the minimal reasoned default, not a source-derived requirement — blocks: 05_API_CONTRACTS.md, 12_FAILURE_RESILIENCE_PLAN.md

## 5. Webhook Contracts

The source workflow describes no inbound or outbound webhook (no callback URL, no external system pushing events into the platform). The closest related concept is principle #31.7 ("Do not directly update core product data from an AI provider callback without backend validation"), which implies AI providers *might* call back asynchronously in some implementations, but no such callback contract is specified.

> ⚠️ Open Question: No webhook contracts are defined in the workflow; if any AI provider or (future) payment gateway is integrated asynchronously via callback, that contract does not yet exist and must be designed when the concrete provider is chosen — blocks: 05_API_CONTRACTS.md, 06_COMMUNICATION_WORKFLOWS.md, 07_QUEUE_AND_CACHE_DESIGN.md

## 6. OpenAPI 3.0 Spec Structure and File Organisation Guidance

Recommended structure, one file per module to mirror the backend package layout (source §29) and keep ownership consistent with `03_SERVICE_BOUNDARIES.md`'s CODEOWNERS:

```text
docs/openapi/
├── openapi.yaml                # root document: info, servers, security schemes, $ref includes
├── components/
│   ├── schemas/
│   │   ├── identity.yaml
│   │   ├── seller.yaml
│   │   ├── catalog.yaml
│   │   ├── media.yaml
│   │   ├── inventory.yaml
│   │   ├── ai.yaml
│   │   ├── pricing.yaml
│   │   ├── market.yaml
│   │   ├── b2b.yaml
│   │   ├── commerce.yaml
│   │   ├── payment.yaml
│   │   ├── fulfillment.yaml     # PROPOSED
│   │   └── experience.yaml      # PROPOSED
│   └── responses/
│       └── errors.yaml          # shared error envelope
└── paths/
    ├── auth.yaml
    ├── sellers.yaml
    ├── products.yaml
    ├── media.yaml
    ├── inventory.yaml
    ├── ai.yaml
    ├── pricing.yaml
    ├── market.yaml
    ├── b2b.yaml
    ├── commerce.yaml
    ├── payment.yaml
    ├── fulfillment.yaml          # PROPOSED
    └── experience.yaml           # PROPOSED
```

Generation approach: hand-authored YAML (not code-first annotation-generated) is recommended so the spec can be reviewed and versioned independently of backend code churn, and so the `docs/openapi` tree can be the literal artifact referenced by `15_REPOSITORY_STRUCTURE.md`'s `docs/` directory.
