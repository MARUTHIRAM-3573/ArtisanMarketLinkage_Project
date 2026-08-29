-- Identity schema: authentication identity, roles, addresses.
-- Source: docs/architecture/04_DATA_MODEL_AND_OWNERSHIP.md §2.1
-- Owning module: backend/modules/auth

CREATE EXTENSION IF NOT EXISTS "pgcrypto"; -- provides gen_random_uuid()

CREATE SCHEMA IF NOT EXISTS identity;

CREATE TABLE identity.users (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email                VARCHAR(255) NOT NULL,
    password_hash        VARCHAR(255) NOT NULL,
    full_name            VARCHAR(255) NOT NULL,
    phone_number         VARCHAR(32),
    account_status       VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    email_verified       BOOLEAN NOT NULL DEFAULT false,
    preferred_language   VARCHAR(16),
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE TABLE identity.roles (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name         VARCHAR(64) NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_roles_name UNIQUE (name)
);

CREATE TABLE identity.user_roles (
    user_id      UUID NOT NULL REFERENCES identity.users(id) ON DELETE CASCADE,
    role_id      UUID NOT NULL REFERENCES identity.roles(id) ON DELETE CASCADE,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (user_id, role_id)
);
CREATE INDEX idx_user_roles_role_id ON identity.user_roles(role_id);

CREATE TABLE identity.addresses (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id          UUID NOT NULL REFERENCES identity.users(id) ON DELETE CASCADE,
    address_type     VARCHAR(32) NOT NULL,
    line1            VARCHAR(255) NOT NULL,
    line2            VARCHAR(255),
    city             VARCHAR(128) NOT NULL,
    state            VARCHAR(128) NOT NULL,
    postal_code      VARCHAR(16) NOT NULL,
    country          VARCHAR(64) NOT NULL,
    is_default       BOOLEAN NOT NULL DEFAULT false,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_addresses_type CHECK (address_type IN ('HOME','WORK','BUSINESS','WAREHOUSE','OTHER'))
);
CREATE INDEX idx_addresses_user_id ON identity.addresses(user_id);

-- Seed reference data: fixed role set (source §8.1 — table, not ENUM, so it remains extensible)
INSERT INTO identity.roles (name) VALUES ('ADMIN'), ('ARTISAN'), ('CUSTOMER'), ('B2B_BUYER');
