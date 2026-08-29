-- Market schema: channels, listings, external marketplace integration surface.
-- Source: docs/architecture/04_DATA_MODEL_AND_OWNERSHIP.md §2.8
-- Owning module: backend/modules/market

CREATE SCHEMA IF NOT EXISTS market;

CREATE TABLE market.market_channels (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code         VARCHAR(16) NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_market_channels_code UNIQUE (code)
);

CREATE TABLE market.market_listings (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id           UUID NOT NULL REFERENCES catalog.products(id) ON DELETE CASCADE,
    market_channel_id    UUID NOT NULL REFERENCES market.market_channels(id),
    status               VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_market_listings_product_channel UNIQUE (product_id, market_channel_id)
);
CREATE INDEX idx_market_listings_product_id ON market.market_listings(product_id);

CREATE TABLE market.external_marketplaces (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name                 VARCHAR(255) NOT NULL,
    integration_mode     VARCHAR(16) NOT NULL,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_external_marketplaces_mode CHECK (integration_mode IN ('MANUAL','API','FILE'))
);

CREATE TABLE market.external_listings (
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    market_listing_id           UUID NOT NULL REFERENCES market.market_listings(id) ON DELETE CASCADE,
    external_marketplace_id     UUID NOT NULL REFERENCES market.external_marketplaces(id),
    external_reference_id       VARCHAR(255),
    sync_status                 VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_external_listings_market_listing_id ON market.external_listings(market_listing_id);

-- Seed reference data: fixed channel set (source §19)
INSERT INTO market.market_channels (code) VALUES ('B2C'), ('B2B'), ('GOVERNMENT');
