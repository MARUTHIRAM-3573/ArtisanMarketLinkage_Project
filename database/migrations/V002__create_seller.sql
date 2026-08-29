-- Seller schema: seller accounts and artisan-specific profile data.
-- Source: docs/architecture/04_DATA_MODEL_AND_OWNERSHIP.md §2.2
-- Owning module: backend/modules/seller

CREATE SCHEMA IF NOT EXISTS seller;

CREATE TABLE seller.sellers (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id                UUID NOT NULL REFERENCES identity.users(id) ON DELETE CASCADE,
    seller_type            VARCHAR(32) NOT NULL,
    display_name           VARCHAR(255) NOT NULL,
    verification_status    VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    created_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_sellers_user_id UNIQUE (user_id),
    CONSTRAINT chk_sellers_type CHECK (seller_type IN ('ARTISAN','COOPERATIVE','SHG','ARTISAN_GROUP','BUSINESS'))
);

CREATE TABLE seller.artisan_profiles (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    seller_id             UUID NOT NULL REFERENCES seller.sellers(id) ON DELETE CASCADE,
    craft_specialty       VARCHAR(255),
    region                VARCHAR(255),
    years_of_experience   INTEGER,
    bio                   TEXT,
    created_at            TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at            TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_artisan_profiles_seller_id UNIQUE (seller_id)
);
