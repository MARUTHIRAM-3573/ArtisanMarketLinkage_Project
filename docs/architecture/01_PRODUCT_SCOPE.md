# 01 — Product Scope

## 1. Executive Summary and Product Vision

The Artisan Digital Commerce Platform is an AI-enabled digital commerce system that helps artisans with limited digital literacy convert traditional products and craft knowledge into digital catalog listings, and connects those listings to three buyer channels: individual consumers (B2C), business buyers (B2B), and government/institutional procurement. The platform is not "an e-commerce website" — its differentiating function is the digitization pipeline that sits in front of commerce: an artisan can speak in their own language about a product, upload a photo, and receive an AI-generated, translated catalog entry with an enhanced product image and a price recommendation, all of which the artisan reviews and approves before it becomes a real, sellable listing.

The product vision, stated directly by the source workflow, is:

> Help artisans with limited digital skills convert their traditional products and knowledge into digital commerce opportunities and connect them with customers, businesses, and institutional/government buyers.

The platform is built as a modular, loosely coupled, extensible system (see `02_ARCHITECTURE_OVERVIEW.md` for the architectural style decision) but is scoped, for its first delivery, as a hackathon MVP: the goal is 100% coverage of the important problem-statement requirements, not 100% of a hypothetical enterprise feature set (source §43).

## 2. Problem Statement

### 2.1 Core Problem

Artisans possess valuable products and craft knowledge but are blocked from digital and institutional commerce by a chain of practical barriers, not by a single missing storefront.

### 2.2 Enumerated Problems (source §2)

1. Limited digital literacy among artisans.
2. Difficulty creating professional digital product catalogs.
3. Language barriers between artisans and digital platforms/buyers.
4. Difficulty producing quality product imagery.
5. Difficulty determining suitable product pricing.
6. Limited access to B2B buyers.
7. Limited access to government/institutional procurement opportunities.
8. Difficulty managing products, SKUs, and inventory digitally.
9. Difficulty handling orders, payments, and fulfillment.
10. Lack of an integrated workflow connecting artisan digitization to actual commerce.

Each of these maps to a functional domain in §4 below, and every domain in the platform exists to close one or more of these ten gaps. No domain is present that does not trace back to this list.

## 3. Target User Personas

The source defines four roles (`identity.roles`: ADMIN, ARTISAN, CUSTOMER, B2B_BUYER) and one channel (government/institutional) that is modeled as a market channel rather than a role, since institutional buyers act through procurement processes rather than authenticated marketplace accounts in the current design.

### 3.1 Artisan (primary persona)

An individual craftsperson, or a representative of a cooperative/SHG/artisan group/small business (`seller.sellers.seller_type`), with limited digital literacy and potentially limited written-language proficiency in the platform's primary interface language. Needs: convert spoken product descriptions into digital listings, get help pricing products fairly, get professional-looking product photography without a photographer, manage stock across product variants, and receive orders from all three channels in one place.

### 3.2 Customer (B2C buyer)

An individual consumer browsing and buying artisan products directly. Needs: a trustworthy catalog with clear pricing and imagery, a standard cart/checkout/payment/delivery experience, and the ability to track and review orders.

### 3.3 B2B Buyer

A business entity (`b2b.b2b_buyers`) seeking to source artisan products in bulk, typically at negotiated pricing. Needs: an inquiry-based sourcing workflow, quotations with validity and terms, and a path from an accepted quotation to a purchase order and, ultimately, a standard commerce order.

### 3.4 Government / Institutional Buyer

An institutional buyer participating through the GOVERNMENT market channel and a procurement-opportunity-to-institutional-purchase flow (source §25). This persona is explicitly **not** given a distinct authentication role in the source data model; institutional procurement is treated as a market channel and order source rather than a new actor type. This is a deliberate design choice (source: "treated as a market channel rather than a completely separate product system") and is preserved as-is.

> ⚠️ Open Question: The source models GOVERNMENT as a market channel and order source_type but does not define who authenticates and acts on behalf of an institutional buyer (a new role, a B2B_BUYER sub-type, or an ADMIN-mediated process) — blocks: 01_PRODUCT_SCOPE.md, 08_SECURITY_AND_VAULT.md, 13_FRONTEND_DASHBOARD_PLAN.md

### 3.5 Admin

Platform operator role (ADMIN). The source does not enumerate admin-specific functional requirements beyond the role's existence in `identity.roles`. Reasoning it through: an admin role in a marketplace with sellers, buyers, B2B negotiation, and payments implies at minimum seller/user verification, dispute oversight, and catalog moderation capability, since these are the natural operational duties of the only elevated role in the system.

> ⚠️ Open Question: No admin-facing functional requirements (moderation, verification approval, dispute handling) are described in the workflow beyond the existence of the ADMIN role — blocks: 01_PRODUCT_SCOPE.md, 13_FRONTEND_DASHBOARD_PLAN.md

## 4. Functional Requirements by Domain

Each domain below corresponds 1:1 to a PostgreSQL schema in `artisan_marketplace` (source §7) and a backend module (source §29).

### 4.1 Identity (`identity` schema / `auth` module)

- FR-ID-01: Register a user with a unique email and a securely hashed password (never plaintext, source §8.1, §33.10, §40).
- FR-ID-02: Authenticate a user and issue a session/token via Spring Security.
- FR-ID-03: Support multiple roles per user via `user_roles` (ADMIN, ARTISAN, CUSTOMER, B2B_BUYER).
- FR-ID-04: Track account status and email verification state.
- FR-ID-05: Store and manage reusable addresses per user, typed as HOME, WORK, BUSINESS, WAREHOUSE, OTHER.

### 4.2 Seller (`seller` schema)

- FR-SL-01: Create a `seller` record linked to a `users` record, distinct from the user's authentication identity.
- FR-SL-02: Support seller types ARTISAN, COOPERATIVE, SHG, ARTISAN_GROUP, BUSINESS without requiring separate commerce logic per type.
- FR-SL-03: Maintain an `artisan_profiles` record holding artisan-specific information for sellers of type ARTISAN (and, by extension, cooperative/SHG/group sellers representing artisans).

### 4.3 Catalog (`catalog` schema)

- FR-CT-01: Maintain a hierarchical category tree via `parent_category_id`.
- FR-CT-02: Create a `product` owned by exactly one seller, optionally assigned to one category.
- FR-CT-03: Keep Product and SKU as distinct concepts — a product may have multiple sellable variants (SKUs) that differ by attributes such as size and color.
- FR-CT-04: Represent product characteristics as flexible attribute-value pairs (`product_attributes`) for domain-specific fields (material, color, technique, weight, origin, etc.) without schema changes per new attribute.
- FR-CT-05: A product does not directly hold stock, media binaries, dynamic pricing, or AI processing results — these are owned by inventory, media, pricing, and ai respectively (source §10, explicit exclusion list).

### 4.4 Media (`media` schema)

- FR-MD-01: Store media asset metadata (type, storage provider, storage path, filename, MIME type, size, checksum, status) in PostgreSQL while binaries live outside the database.
- FR-MD-02: Support media types IMAGE, VIDEO, AUDIO, DOCUMENT.
- FR-MD-03: Support storage providers LOCAL, S3, OTHER behind a `MediaStorageService` abstraction.
- FR-MD-04: Associate multiple media assets with a single product via `product_media`, each tagged with a purpose: PRODUCT_IMAGE, THUMBNAIL, AI_ENHANCED, MARKETPLACE_IMAGE, OTHER.

### 4.5 Inventory (`inventory` schema)

- FR-IV-01: Track current stock state per SKU: `available_quantity`, `reserved_quantity`, `reorder_level`.
- FR-IV-02: Record every stock change as an immutable movement: STOCK_IN, SALE, RESERVATION, RELEASE, RETURN, ADJUSTMENT, DAMAGE.
- FR-IV-03: Support reservation of stock ahead of sale completion (required for both B2C checkout holds and B2B bulk purchase commitments).
- FR-IV-04: Preserve full stock history for audit purposes, independent of the current-state table.

### 4.6 AI (`ai` schema)

- FR-AI-01: Accept a voice recording from an artisan and store it as a media asset, then as an `ai.voice_inputs` record.
- FR-AI-02: Run Speech-to-Text on a voice input and persist the transcription (`ai.speech_transcriptions`).
- FR-AI-03: Translate the transcription and persist the result (`ai.translations`), supporting local-language artisan input becoming translated digital catalog content.
- FR-AI-04: Generate a draft catalog entry (title, description, attributes) from the translated text and persist it (`ai.catalog_generations`) for artisan review before it becomes a `catalog.products` record.
- FR-AI-05: Process a product image for background removal, lighting enhancement, and e-commerce formatting, persisting the result (`ai.image_processing_results`) and storing the output image as a new media asset.
- FR-AI-06: Produce a dynamic price recommendation (`ai.price_recommendations`) from cost, market, demand, and product signals, for seller review before it becomes an actual SKU price.
- FR-AI-07: Track every AI invocation as a job (`ai.ai_jobs`) with enough metadata to reproduce and audit the run (model name/version where available, per source §31.4).
- FR-AI-08: No AI output may write directly to a core business entity (`catalog.products`, `pricing.sku_prices`, etc.) without passing through backend validation and, where the workflow specifies, explicit artisan/user approval (source §14, §31.6–31.7). This is a hard functional constraint, not a preference.

### 4.7 Pricing (`pricing` schema)

- FR-PR-01: Record itemized cost inputs per product/SKU: raw material cost, labour cost, other costs (`pricing.cost_records`).
- FR-PR-02: Store external/reference market pricing data (`pricing.market_prices`) as an input signal to recommendations, not as an authoritative price.
- FR-PR-03: Store the actual, seller-approved prices applied to SKUs (`pricing.sku_prices`), typed SELLING, MRP, or WHOLESALE, with validity periods so historical pricing is preserved.
- FR-PR-04: An AI price recommendation is never automatically the final selling price; it requires seller review (source §17).

### 4.8 Market (`market` schema)

- FR-MK-01: List a product through one or more market channels: B2C, B2B, GOVERNMENT — a single product may be simultaneously listed on all three.
- FR-MK-02: Support future external marketplace integrations (`market.external_marketplaces`, `market.external_listings`) via MANUAL, API, or FILE integration modes, without coupling the core product/order model to any specific external marketplace.

### 4.9 B2B (`b2b` schema)

- FR-B2-01: Register a B2B buyer's organization details (name, organization type, tax identifier, verification status).
- FR-B2-02: Allow a B2B buyer to submit an inquiry against a seller's product (quantity, target price, message, delivery requirement, status).
- FR-B2-03: Allow a seller to respond with a quotation (quotation number, quantity, unit price, total, validity, terms, status).
- FR-B2-04: Support negotiation leading to an accepted quotation.
- FR-B2-05: Convert an accepted quotation into a purchase order, which is the buyer's accepted commercial request prior to entering the common commerce order flow.
- FR-B2-06: A purchase order must resolve into a `commerce.orders` record — B2B does not have a parallel, separate order table (source §21 principle).

### 4.10 Commerce (`commerce` schema)

- FR-CM-01: Support a standard B2C cart (`carts`, `cart_items`) and checkout flow that produces an order.
- FR-CM-02: Provide one common order model (`orders`, `order_items`, `order_status_history`) for B2C, B2B, and GOVERNMENT order sources, distinguished by `source_type` and `source_reference_id`, not by separate tables (source §21, and hard constraint in §40: "Do not create separate B2C Product and B2B Product tables" extends by principle to orders).
- FR-CM-03: Snapshot product/SKU naming information onto each order item at time of order, so historical orders remain accurate even if the live product record later changes.
- FR-CM-04: Track order status transitions (PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED) as an explicit history, separate from the order's current status field.

### 4.11 Payment (`payment` schema)

- FR-PY-01: Create a payment against an order and track it through one or more payment transactions, each resolving to SUCCESS or FAILED.
- FR-PY-02: Generate an invoice once payment succeeds.
- FR-PY-03: Compute and record seller settlement (order value minus platform commission).
- FR-PY-04: Support refunds against a payment, resulting in the customer receiving a refund.
- FR-PY-05: Never store card numbers, CVV, or full payment credentials — only payment references and transaction metadata (hard constraint, source §22, §40).
- FR-PY-06: For the MVP, a mock payment gateway is acceptable in place of a real payment processor.

### 4.12 Fulfillment (`fulfillment` schema) — **PROPOSED, pending team approval**

Source §23 explicitly marks this domain "STATUS: TO BE IMPLEMENTED" and states "exact implementation must be agreed before creation." The following functional requirements are reasoned from the "likely entities" the source already lists, and are marked PROPOSED per architecture principle #17 until approved.

- FR-FL-01 (PROPOSED): Create a fulfillment record against a confirmed order.
- FR-FL-02 (PROPOSED): Create one or more shipments against a fulfillment, each with shipment items referencing order items.
- FR-FL-03 (PROPOSED): Record delivery events (e.g., dispatched, in transit, delivered, delivery failed) against a shipment.
- FR-FL-04 (PROPOSED): Fulfillment status changes should feed order status transitions (SHIPPED, DELIVERED) in `commerce.order_status_history`, since the end-to-end workflow diagram (source §26) places Fulfillment → Shipment → Delivery between Payment/Invoice and Review.

> ⚠️ Open Question: Fulfillment schema, entities, and status vocabulary are explicitly marked TO BE IMPLEMENTED / must be agreed by the team before creation — blocks: 01_PRODUCT_SCOPE.md, 04_DATA_MODEL_AND_OWNERSHIP.md, 03_SERVICE_BOUNDARIES.md, 06_COMMUNICATION_WORKFLOWS.md, 12_FAILURE_RESILIENCE_PLAN.md, 17_CODING_BACKLOG.md

### 4.13 Experience (`experience` schema) — **PROPOSED, pending team approval**

Source §24 also marks this "STATUS: TO BE IMPLEMENTED," listing product reviews, ratings, customer feedback, "possibly" favorites/wishlist, and customer-facing order experience as planned capabilities, with a potential schema of `experience.reviews` and `experience.ratings` only.

- FR-EX-01 (PROPOSED): Allow a customer to submit a review and/or rating against a delivered order item / product.
- FR-EX-02 (PROPOSED): Surface aggregate ratings on product listings.
- FR-EX-03 (PROPOSED, uncertain): Favorites/wishlist — the source uses the word "possibly," so this is not a committed requirement.

> ⚠️ Open Question: Experience schema is TO BE IMPLEMENTED; whether favorites/wishlist is in scope at all is explicitly uncertain in the source ("possibly"), and no entity for it is listed in the "potential schema" — blocks: 01_PRODUCT_SCOPE.md, 04_DATA_MODEL_AND_OWNERSHIP.md, 13_FRONTEND_DASHBOARD_PLAN.md

### 4.14 Government / Institutional Procurement (cross-cutting, `market` schema)

- FR-GV-01: Represent government/institutional procurement as a market channel (GOVERNMENT) plus a procurement-opportunity-to-institutional-purchase flow that resolves into a standard `commerce.orders` record (source §25).
- FR-GV-02: Do not hard-code a specific external government marketplace/API into core product logic — this channel must remain adaptable to future integrations, consistent with the `market.external_marketplaces` abstraction.

## 5. Non-Functional Requirements

The source workflow document does not state explicit numeric SLOs, uptime targets, or compliance regimes. The NFRs below are reasoned from the platform's stated nature (hackathon MVP, mobile-first, artisan users with limited connectivity/literacy, handling PII and payment references) and are marked accordingly.

### 5.1 Performance

- NFR-P-01 (PROPOSED): Mobile app cold start and API response times should feel instantaneous on a typical Android device over 3G/4G, since the primary user base (artisans) may have limited connectivity — no numeric target is given in source.
- NFR-P-02 (PROPOSED): AI operations (STT, translation, catalog generation, image enhancement, pricing) are expected to be the slowest steps in any flow and should be handled as asynchronous jobs with a job-status pattern (`ai.ai_jobs`) rather than blocking synchronous requests, consistent with the AI job tracking table already in the data model.

> ⚠️ Open Question: No performance SLOs, target concurrent users, or expected transaction volumes are specified in the workflow — blocks: 01_PRODUCT_SCOPE.md, 09_TESTING_STRATEGY.md, 11_OBSERVABILITY_AND_INCIDENTS.md

### 5.2 Availability

- NFR-A-01 (PROPOSED): Standard single-region availability is sufficient for the hackathon MVP; the source states deployment can use "a suitable cloud/server environment" with the exact provider left as an implementation decision, implying no multi-region or high-availability requirement was intended for this phase.

### 5.3 Scalability

- NFR-S-01: The modular monolith architecture (see `02_ARCHITECTURE_OVERVIEW.md`) is explicitly designed so each domain module can later be extracted into an independently scalable service without redesigning the data model (source §9, "allows future support... without redesigning the commerce system"; §44 "extensible").

### 5.4 Security

- NFR-SC-01: Passwords must never be stored in plaintext (source §8.1, §33.10, §40) — hashed only.
- NFR-SC-02: Card numbers, CVV, and full payment credentials must never be stored (source §22, §40).
- NFR-SC-03: Frontend must never connect directly to PostgreSQL; all access is via backend REST APIs (source §4.6, §27, §40).
- NFR-SC-04: Media binaries are never stored in PostgreSQL; only metadata and storage references (source §12, §32.1).
- NFR-SC-05: Uploaded user files must never be committed to Git (source §32.8, §35).
- NFR-SC-06: MIME type and file size must be validated on upload (source §32.5).

### 5.5 Compliance

The source workflow does not name a specific compliance regime (GDPR, SOC2, PCI-DSS, or a local data-protection law). Given the platform handles PII (identity, addresses) and payment references, and may serve Indian artisans and buyers (category example "Andhra Pradesh," Indian Rupee symbol ₹ used in §22 example), the most directly implied compliance surface is:

- NFR-CP-01 (PROPOSED): Basic data-protection hygiene consistent with India's Digital Personal Data Protection framework and general PII best practice (data minimization, hashed passwords, no plaintext payment data).
- NFR-CP-02 (PROPOSED): PCI-DSS scope is deliberately minimized by design, since no card data is ever stored — this pushes PCI obligations onto the (future) real payment gateway rather than the platform itself.

> ⚠️ Open Question: No specific compliance regime (GDPR, SOC2, PCI-DSS, India DPDP Act, etc.) is named in the workflow — blocks: 01_PRODUCT_SCOPE.md, 08_SECURITY_AND_VAULT.md

## 6. Explicit Out-of-Scope Items

Derived from the source's "What Must NOT Happen" (§40), hackathon MVP principle (§43), and current implementation status (§37):

- A second/duplicate Product entity, or separate B2C/B2B product tables.
- Storing images or other media binaries directly in PostgreSQL.
- Storing plaintext passwords or full payment card data.
- Direct frontend-to-PostgreSQL connectivity.
- AI directly modifying core business entities without backend validation and required approval.
- Replacing PostgreSQL or Spring Boot without explicit team approval.
- Multiple competing architectures within the same system.
- A real (non-mock) payment gateway integration for the MVP (mock gateway is explicitly acceptable).
- Google Play Store distribution for the MVP (APK/direct install is the priority target; store release is a later step "if time permits").
- A production-grade object storage (S3) migration for the MVP (local storage is current; S3 is future-compatible only, behind an abstraction).
- Non-essential enterprise features that do not improve coverage of the ten core problems in §2.2.

## 7. Acceptance Criteria per Major Feature Area

### 7.1 Artisan Digitization (Voice → Catalog)

- [ ] An artisan can record voice input from the mobile app and it is stored as a media asset and an `ai.voice_inputs` record.
- [ ] The voice input produces a speech transcription and a translation, both persisted and retrievable.
- [ ] An AI-generated catalog draft is produced from the translation and shown to the artisan for review before any `catalog.products` record is created.
- [ ] No `catalog.products` record is created without an artisan approval step.

### 7.2 AI Image Studio

- [ ] An artisan can upload a product image and receive a version with background removed, lighting enhanced, and formatted for e-commerce.
- [ ] The processing result is recorded in `ai.image_processing_results` and the enhanced output is stored as a new media asset linked to the product via `product_media` with purpose `AI_ENHANCED`.

### 7.3 AI Dynamic Pricing

- [ ] A price recommendation can be generated from cost records, market prices, and product information.
- [ ] The recommendation is persisted in `ai.price_recommendations` and never becomes an active `pricing.sku_prices` entry without seller review/approval.

### 7.4 Product & Inventory Management

- [ ] A seller can create a product, attach one or more SKUs with distinguishing attributes, and each SKU has an independent inventory record.
- [ ] Every stock change produces an `inventory_movements` record, and `available_quantity`/`reserved_quantity` on `inventories` reflects the net of those movements.

### 7.5 B2C Commerce

- [ ] A customer can add SKUs to a cart, check out, and produce an order with `source_type = B2C`.
- [ ] Order items snapshot product/SKU display data at time of order.

### 7.6 B2B Commerce

- [ ] A B2B buyer can submit an inquiry, receive a quotation, and an accepted quotation produces a purchase order that resolves into a `commerce.orders` record with `source_type = B2B`.

### 7.7 Government/Institutional Channel

- [ ] A product can be listed under the GOVERNMENT market channel independent of its B2C/B2B listing state.
- [ ] An institutional purchase produces a `commerce.orders` record with `source_type = GOVERNMENT`.

### 7.8 Payment

- [ ] An order can be paid via the mock payment gateway, producing a payment and a transaction resolving to SUCCESS or FAILED.
- [ ] A successful payment produces an invoice; a refund can be issued against a payment.

### 7.9 Fulfillment (PROPOSED, pending approval per §4.12)

- [ ] Once approved, a confirmed order can be tracked through fulfillment → shipment → delivery, updating `commerce.order_status_history`.

## 8. MVP Boundary

### 8.1 In MVP (source §43, ordered as given)

1. Artisan onboarding
2. Product digitization
3. Voice → Speech-to-Text
4. Translation
5. AI catalog generation
6. Image enhancement
7. AI pricing recommendation
8. Product/SKU management
9. Inventory
10. B2C marketplace
11. B2B inquiry/quotation flow
12. Government/institutional market representation
13. Order
14. Payment/mock payment
15. Fulfillment/order tracking

### 8.2 Deferred Beyond MVP

- Customer Experience domain (reviews, ratings, favorites/wishlist) — explicitly "TO BE IMPLEMENTED," not in the MVP priority list.
- Real (non-mock) payment gateway integration.
- Object storage (S3) migration from local file storage.
- External marketplace integrations (MANUAL/API/FILE) beyond the abstraction existing in the schema.
- Google Play Store distribution.
- iOS build/release (architecture is kept iOS-compatible; an actual iOS build is not the hackathon deployment target).
- Any distributed/microservices decomposition of the modular monolith.

## 9. Requirements Traceability

Every functional requirement above traces to a named section of the source workflow document; none were invented. The two domains carried as PROPOSED (Fulfillment, Experience) are carried through **every** downstream document consistently as PROPOSED, and their open questions are repeated at the point of use rather than resolved silently, per architecture principle #17 ("Any proposed architecture change must be clearly marked PROPOSED until approved").
