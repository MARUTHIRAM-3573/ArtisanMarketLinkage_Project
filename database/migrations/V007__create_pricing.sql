-- Pricing schema: cost inputs, market reference prices, actual SKU prices.
-- Source: docs/architecture/04_DATA_MODEL_AND_OWNERSHIP.md §2.7
-- Owning module: backend/modules/pricing

CREATE SCHEMA IF NOT EXISTS pricing;

CREATE TABLE pricing.cost_records (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id             UUID NOT NULL REFERENCES catalog.products(id) ON DELETE CASCADE,
    raw_material_cost      NUMERIC(12,2) NOT NULL DEFAULT 0,
    labour_cost            NUMERIC(12,2) NOT NULL DEFAULT 0,
    other_cost             NUMERIC(12,2) NOT NULL DEFAULT 0,
    created_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_cost_records_product_id ON pricing.cost_records(product_id);

CREATE TABLE pricing.market_prices (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id             UUID NOT NULL REFERENCES catalog.products(id) ON DELETE CASCADE,
    reference_price        NUMERIC(12,2) NOT NULL,
    source_description     VARCHAR(255),
    observed_at            TIMESTAMPTZ NOT NULL,
    created_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_market_prices_product_id ON pricing.market_prices(product_id);

CREATE TABLE pricing.sku_prices (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_sku_id     UUID NOT NULL REFERENCES catalog.product_skus(id) ON DELETE CASCADE,
    price_type         VARCHAR(16) NOT NULL,
    amount             NUMERIC(12,2) NOT NULL,
    valid_from         TIMESTAMPTZ NOT NULL,
    valid_to           TIMESTAMPTZ,
    created_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_sku_prices_type CHECK (price_type IN ('SELLING','MRP','WHOLESALE'))
);
CREATE INDEX idx_sku_prices_sku_type_valid_from ON pricing.sku_prices(product_sku_id, price_type, valid_from);
