-- Commerce schema: cart, orders — the ONE shared order pipeline for B2C/B2B/GOVERNMENT.
-- Source: docs/architecture/04_DATA_MODEL_AND_OWNERSHIP.md §2.10
-- Owning module: backend/modules/commerce

CREATE SCHEMA IF NOT EXISTS commerce;

CREATE TABLE commerce.carts (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      UUID NOT NULL REFERENCES identity.users(id) ON DELETE CASCADE,
    status       VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_carts_status CHECK (status IN ('ACTIVE','CHECKED_OUT','ABANDONED'))
);
CREATE INDEX idx_carts_user_id ON commerce.carts(user_id);

CREATE TABLE commerce.cart_items (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cart_id           UUID NOT NULL REFERENCES commerce.carts(id) ON DELETE CASCADE,
    product_sku_id    UUID NOT NULL REFERENCES catalog.product_skus(id),
    quantity          INTEGER NOT NULL,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_cart_items_quantity_positive CHECK (quantity > 0)
);
CREATE INDEX idx_cart_items_cart_id ON commerce.cart_items(cart_id);

CREATE TABLE commerce.orders (
    id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id                  UUID NOT NULL REFERENCES identity.users(id),
    source_type              VARCHAR(16) NOT NULL,
    source_reference_id      UUID, -- soft reference, no FK: can point to b2b.purchase_orders or nothing (B2C) — see principle #15
    status                   VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    total_amount             NUMERIC(12,2) NOT NULL,
    shipping_address_id      UUID NOT NULL REFERENCES identity.addresses(id),
    created_at               TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at               TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_orders_source_type CHECK (source_type IN ('B2C','B2B','GOVERNMENT')),
    CONSTRAINT chk_orders_status CHECK (status IN ('PENDING','CONFIRMED','PROCESSING','SHIPPED','DELIVERED'))
    -- NOTE: no CANCELLED/RETURNED state exists in the source enum despite payment.refunds
    -- existing — see docs/architecture/04_DATA_MODEL_AND_OWNERSHIP.md open question.
);
CREATE INDEX idx_orders_user_id ON commerce.orders(user_id);

CREATE TABLE commerce.order_items (
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id                    UUID NOT NULL REFERENCES commerce.orders(id) ON DELETE CASCADE,
    product_sku_id              UUID NOT NULL REFERENCES catalog.product_skus(id),
    product_name_snapshot       VARCHAR(255) NOT NULL,
    sku_variant_snapshot        VARCHAR(255) NOT NULL,
    unit_price_snapshot         NUMERIC(12,2) NOT NULL,
    quantity                    INTEGER NOT NULL,
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_order_items_quantity_positive CHECK (quantity > 0)
);
CREATE INDEX idx_order_items_order_id ON commerce.order_items(order_id);

CREATE TABLE commerce.order_status_history (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id           UUID NOT NULL REFERENCES commerce.orders(id) ON DELETE CASCADE,
    status             VARCHAR(32) NOT NULL,
    transitioned_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_order_status_history_order_id ON commerce.order_status_history(order_id);

-- Follow-up FK now that commerce.orders exists (deferred from V009 to avoid a forward reference)
ALTER TABLE b2b.purchase_orders
    ADD CONSTRAINT fk_purchase_orders_order_id
    FOREIGN KEY (order_id) REFERENCES commerce.orders(id) ON DELETE SET NULL;
