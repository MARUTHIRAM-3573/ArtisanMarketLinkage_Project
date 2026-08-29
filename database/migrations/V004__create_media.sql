-- Media schema: media asset metadata + product-media association.
-- Binaries are NEVER stored here (source §11, §12, principle #12/#13) — metadata/reference only.
-- Source: docs/architecture/04_DATA_MODEL_AND_OWNERSHIP.md §2.4
-- Owning module: backend/modules/media

CREATE SCHEMA IF NOT EXISTS media;

CREATE TABLE media.media_assets (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    media_type           VARCHAR(16) NOT NULL,
    storage_provider     VARCHAR(16) NOT NULL,
    storage_path         VARCHAR(1024) NOT NULL,
    original_filename    VARCHAR(255) NOT NULL,
    mime_type            VARCHAR(128) NOT NULL,
    file_size_bytes      BIGINT NOT NULL,
    checksum             VARCHAR(128) NOT NULL,
    status               VARCHAR(32) NOT NULL DEFAULT 'UPLOADED',
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_media_assets_type CHECK (media_type IN ('IMAGE','VIDEO','AUDIO','DOCUMENT')),
    CONSTRAINT chk_media_assets_provider CHECK (storage_provider IN ('LOCAL','S3','OTHER'))
);

CREATE TABLE media.product_media (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id       UUID NOT NULL REFERENCES catalog.products(id) ON DELETE CASCADE,
    media_asset_id   UUID NOT NULL REFERENCES media.media_assets(id) ON DELETE CASCADE,
    purpose          VARCHAR(32) NOT NULL,
    display_order    INTEGER NOT NULL DEFAULT 0,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_product_media_purpose CHECK (purpose IN ('PRODUCT_IMAGE','THUMBNAIL','AI_ENHANCED','MARKETPLACE_IMAGE','OTHER'))
);
CREATE INDEX idx_product_media_product_id ON media.product_media(product_id);
CREATE INDEX idx_product_media_media_asset_id ON media.product_media(media_asset_id);
