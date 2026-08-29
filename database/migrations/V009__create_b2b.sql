-- B2B schema: buyers, inquiries, quotations, purchase orders.
-- Source: docs/architecture/04_DATA_MODEL_AND_OWNERSHIP.md §2.9
-- Owning module: backend/modules/b2b
-- NOTE: purchase_orders.order_id references commerce.orders, created in V010 —
-- the FK is added as a follow-up ALTER at the end of V010__create_commerce.sql.

CREATE SCHEMA IF NOT EXISTS b2b;

CREATE TABLE b2b.b2b_buyers (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id                UUID NOT NULL REFERENCES identity.users(id) ON DELETE CASCADE,
    organization_name      VARCHAR(255) NOT NULL,
    organization_type      VARCHAR(64),
    tax_identifier         VARCHAR(64),
    verification_status    VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    created_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_b2b_buyers_user_id UNIQUE (user_id)
);

CREATE TABLE b2b.b2b_inquiries (
    id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    b2b_buyer_id             UUID NOT NULL REFERENCES b2b.b2b_buyers(id) ON DELETE CASCADE,
    seller_id                UUID NOT NULL REFERENCES seller.sellers(id),
    product_id               UUID NOT NULL REFERENCES catalog.products(id),
    requested_quantity       INTEGER NOT NULL,
    target_price             NUMERIC(12,2),
    message                  TEXT,
    delivery_requirement     TEXT,
    status                   VARCHAR(32) NOT NULL DEFAULT 'OPEN',
    created_at               TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at               TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_b2b_inquiries_buyer_id ON b2b.b2b_inquiries(b2b_buyer_id);
CREATE INDEX idx_b2b_inquiries_seller_id ON b2b.b2b_inquiries(seller_id);
CREATE INDEX idx_b2b_inquiries_product_id ON b2b.b2b_inquiries(product_id);

CREATE TABLE b2b.b2b_quotations (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    b2b_inquiry_id       UUID NOT NULL REFERENCES b2b.b2b_inquiries(id) ON DELETE CASCADE,
    quotation_number     VARCHAR(64) NOT NULL,
    seller_id            UUID NOT NULL REFERENCES seller.sellers(id),
    quantity             INTEGER NOT NULL,
    unit_price           NUMERIC(12,2) NOT NULL,
    total_amount         NUMERIC(12,2) NOT NULL,
    validity_date        TIMESTAMPTZ NOT NULL,
    terms                TEXT,
    status               VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_b2b_quotations_number UNIQUE (quotation_number)
);
CREATE INDEX idx_b2b_quotations_inquiry_id ON b2b.b2b_quotations(b2b_inquiry_id);

CREATE TABLE b2b.purchase_orders (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    b2b_quotation_id       UUID NOT NULL REFERENCES b2b.b2b_quotations(id) ON DELETE CASCADE,
    order_id               UUID, -- FK added in V010 follow-up ALTER, once commerce.orders exists
    status                 VARCHAR(32) NOT NULL DEFAULT 'ACCEPTED',
    created_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_purchase_orders_quotation_id ON b2b.purchase_orders(b2b_quotation_id);
