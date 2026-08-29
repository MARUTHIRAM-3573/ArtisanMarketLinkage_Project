-- Catalog schema: categories, products, SKUs, attributes.
-- Source: docs/architecture/04_DATA_MODEL_AND_OWNERSHIP.md §2.3
-- Owning module: backend/modules/catalog

CREATE SCHEMA IF NOT EXISTS catalog;

CREATE TABLE catalog.categories (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name                 VARCHAR(255) NOT NULL,
    parent_category_id  UUID REFERENCES catalog.categories(id) ON DELETE SET NULL,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_categories_parent_id ON catalog.categories(parent_category_id);

-- catalog.products has no FK to ai schema here to avoid a forward reference across
-- migration ordering; source_catalog_generation_id is added as a nullable soft
-- reference once the ai schema exists (see V006__create_ai.sql for the follow-up ALTER).
CREATE TABLE catalog.products (
    id                              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    seller_id                       UUID NOT NULL REFERENCES seller.sellers(id) ON DELETE CASCADE,
    category_id                     UUID REFERENCES catalog.categories(id) ON DELETE SET NULL,
    title                           VARCHAR(255) NOT NULL,
    description                     TEXT,
    status                          VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
    source_catalog_generation_id    UUID,
    created_at                      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_products_status CHECK (status IN ('DRAFT','ACTIVE','INACTIVE'))
);
CREATE INDEX idx_products_seller_id ON catalog.products(seller_id);
CREATE INDEX idx_products_category_id ON catalog.products(category_id);

CREATE TABLE catalog.product_skus (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id      UUID NOT NULL REFERENCES catalog.products(id) ON DELETE CASCADE,
    sku_code        VARCHAR(64) NOT NULL,
    variant_label   VARCHAR(255) NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_product_skus_sku_code UNIQUE (sku_code)
);
CREATE INDEX idx_product_skus_product_id ON catalog.product_skus(product_id);

CREATE TABLE catalog.product_attributes (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id        UUID NOT NULL REFERENCES catalog.products(id) ON DELETE CASCADE,
    attribute_name    VARCHAR(128) NOT NULL,
    attribute_value   VARCHAR(255) NOT NULL,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_product_attributes_product_id_name ON catalog.product_attributes(product_id, attribute_name);
