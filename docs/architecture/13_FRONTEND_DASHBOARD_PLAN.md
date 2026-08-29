# 13 — Frontend / Dashboard Plan

The mobile app is React Native + TypeScript + Expo, Android-first (source §5, §27, §28). "Dashboard" here means each persona's home/management screens, not a web admin dashboard — no web frontend exists in this architecture (source §5: "React (web) is not the primary frontend").

## 1. User Personas and Their Dashboard Views

| Persona | Primary dashboard view |
|---|---|
| Artisan | "My Products" — list of own products with status (draft/active), quick actions to start a new voice/image digitization flow, pending AI review items surfaced prominently, order queue for their products |
| Customer | "Marketplace Home" — browse/search/category navigation, cart badge, order history |
| B2B Buyer | "My Inquiries" — inquiry list with status, quotations awaiting response, purchase orders |
| Admin | Moderation/verification queue (per open question in `01_PRODUCT_SCOPE.md` §3.5 — admin requirements are reasoned, not explicit) |

> ⚠️ Open Question: No admin-facing screen requirements exist in source at all — blocks: 13_FRONTEND_DASHBOARD_PLAN.md

## 2. Page Hierarchy and Routing Structure

Derived directly from source §27's frontend responsibility list:

```text
/                                → Auth gate (redirect to /login or /home)
/login, /register                → Authentication
/home                             → Marketplace Home (Customer) / My Products (Artisan) / My Inquiries (B2B Buyer)
/products                         → Marketplace browse (Customer-facing, also visible to all roles)
/products/:id                     → Product detail
/seller/profile                   → Seller Profile (create/edit)
/seller/products                  → Product Management (list, create, edit)
/seller/products/new/voice        → Voice Input → AI Catalog Review flow
/seller/products/:id/image-studio  → Image Upload → AI Image Review flow
/seller/products/:id/pricing       → Pricing Review (cost records, AI recommendation, accept/override)
/seller/inventory                  → Inventory management across own SKUs
/b2b/inquiries                     → B2B inquiry list (buyer) / incoming inquiries (seller)
/b2b/inquiries/:id                 → Inquiry detail, quotation thread
/cart                               → Cart
/checkout                           → Checkout
/orders                             → Order Tracking (list)
/orders/:id                          → Order detail + status history
/payment/:orderId                    → Payment
```

## 3. Component Hierarchy and Reuse Strategy

```text
App
├── AuthGate (token check, redirect)
├── Navigation (role-aware tab/stack navigator — tabs differ by active role)
├── screens/
│   ├── auth/ (Login, Register)
│   ├── artisan/ (ProductList, ProductForm, VoiceCapture, AICatalogReview, ImageStudio, AIImageReview, PricingReview, InventoryManager)
│   ├── marketplace/ (Home, ProductDetail, CategoryBrowse)
│   ├── b2b/ (InquiryList, InquiryDetail, QuotationThread)
│   ├── commerce/ (Cart, Checkout, OrderList, OrderDetail)
│   └── payment/ (PaymentScreen)
├── components/ (shared, reused across screens)
│   ├── ProductCard, PriceTag, StatusBadge, MediaUploader, AIJobStatusIndicator
│   └── forms/ (shared form inputs, validation wrappers)
└── services/ (API client modules, one per backend module — mirrors `05_API_CONTRACTS.md`)
```

Reuse strategy: `ProductCard` and `StatusBadge` are shared across marketplace browse, seller product list, and order item displays; `AIJobStatusIndicator` is shared across all three AI review screens (catalog, image, pricing) since they share the same underlying job-polling pattern (`ai.ai_jobs`).

## 4. State Management Approach

No state management library is named in source. **Reasoned choice:** React Query (TanStack Query) for server state (API data, caching, polling AI job status) plus React Context or a lightweight store (Zustand) for local UI/session state (current auth token, active role if a user has multiple roles).

**Justification:** the app's dominant state-management need, per the functional requirements, is server-state synchronization with polling (AI job status) and cache invalidation on write (cart, inventory) — this is exactly what React Query is built for, and it avoids the boilerplate of a full Redux setup for an app of this scope, consistent with principle #2 applied to the frontend.

> ⚠️ Open Question: No state management library is specified in source — blocks: 13_FRONTEND_DASHBOARD_PLAN.md, 16_ADR_PACK.md

## 5. API Consumption Patterns

| Pattern | When used |
|---|---|
| REST (request/response) | All standard CRUD — products, cart, orders, payments, B2B, etc. |
| REST + client-side polling | AI job status (`GET /api/v1/ai/jobs/{id}`) — polled at a short interval (PROPOSED: every 2-3s) until `status` reaches a terminal state, since no WebSocket/SSE/push mechanism is named in source (open question already flagged in `06_COMMUNICATION_WORKFLOWS.md` §1). |
| WebSocket / SSE | Not used — not mentioned or implied anywhere in source; polling is the minimal-new-technology substitute. |

## 6. Authentication Flow

Per `08_SECURITY_AND_VAULT.md` Part C.4: token stored in secure on-device storage (Expo SecureStore), attached as `Authorization: Bearer` header via a shared API client interceptor, silent refresh on 401, redirect to `/login` on refresh failure, clearing stored tokens on logout.

## 7. Design System and Component Library Choice

Not named in source (§27 lists functional screen areas, not visual/component tooling). **Reasoned choice:** React Native Paper (Material Design components) — chosen because it is Expo-compatible out of the box, requires no native linking step (important for the APK-first hackathon build target, source §28), and its Material Design defaults suit an Android-first app without requiring the team to design a custom design system from scratch during a hackathon.

> ⚠️ Open Question: No design system or component library is specified in source — blocks: 13_FRONTEND_DASHBOARD_PLAN.md, 16_ADR_PACK.md

## 8. Frontend Performance Targets

Not specified in source. Reasoned defaults appropriate to an Android-first hackathon app targeting users who may have limited connectivity (per artisan persona in `01_PRODUCT_SCOPE.md` §3.1):

| Metric | Target (PROPOSED) |
|---|---|
| APK bundle size | As small as practical; avoid unnecessary native module dependencies given direct-APK-install distribution (no Play Store size optimization pipeline for MVP) |
| Time to Interactive (TTI) | < 3s on a mid-range Android device |
| Largest Contentful Paint (LCP)-equivalent (first meaningful screen render) | < 2s |

> ⚠️ Open Question: No frontend performance targets are specified in source — blocks: 13_FRONTEND_DASHBOARD_PLAN.md, 09_TESTING_STRATEGY.md

## 9. Error Handling and Empty State Strategy per Page

| Page | Error state | Empty state |
|---|---|---|
| Marketplace Home / Browse | Retry banner on network failure | "No products found" with filter-reset action |
| Product Detail | "Product unavailable" if 404 | N/A |
| Voice Capture / AI Catalog Review | Explicit "AI is taking longer than usual" after a polling timeout threshold, with manual-entry fallback (per open question in `12_FAILURE_RESILIENCE_PLAN.md` §4) | "No draft yet — record your product to get started" |
| Image Studio | "Enhancement failed, try again or use original photo" | N/A |
| Pricing Review | "No recommendation yet — add cost records first" | Empty cost-records prompt |
| Cart | Stock-conflict error surfaced per line item on checkout failure | "Your cart is empty" with browse CTA |
| Order Tracking | "Unable to load order" retry | "No orders yet" |
| B2B Inquiry List | Retry on load failure | "No inquiries yet" (buyer) / "No incoming inquiries" (seller) |

> ⚠️ Open Question: None of the specific error/empty-state copy or UX behavior is specified in source — the table above is reasoned UX best practice applied to the known screens — blocks: 13_FRONTEND_DASHBOARD_PLAN.md
