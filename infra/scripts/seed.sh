#!/usr/bin/env bash
set -euo pipefail

# infra/scripts/seed.sh
#
# Reference data (roles, market channels) is already seeded by the Flyway
# migrations themselves (database/migrations/V001__create_identity.sql,
# V008__create_market.sql) — Spring Boot runs those on every backend
# startup, so there is nothing to do for that data here.
#
# This script instead seeds a small set of DEV-ONLY demo records so a fresh
# clone has something to look at immediately: one demo artisan seller and
# one demo product. Safe to re-run — every insert is idempotent
# (ON CONFLICT DO NOTHING).
#
# Demo login: demo.artisan@artisanplatform.dev / Password123!

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$REPO_ROOT"

DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-artisan_marketplace}"
DB_USERNAME="${DB_USERNAME:-artisan}"
DB_PASSWORD="${DB_PASSWORD:-artisan_dev_password}"

echo "==> Seeding demo data into ${DB_NAME}@${DB_HOST}:${DB_PORT}"

PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USERNAME" -d "$DB_NAME" -v ON_ERROR_STOP=1 <<'SQL'
DO $$
DECLARE
  demo_user_id UUID;
  demo_seller_id UUID;
BEGIN
  INSERT INTO identity.users (email, password_hash, full_name, account_status, email_verified)
  VALUES (
    'demo.artisan@artisanplatform.dev',
    -- bcrypt hash of "Password123!" — DEV ONLY, never a real credential.
    '$2b$10$FxE6U0dMkoDASAx7N.2dYecE9ijBKOwM2i/96vI0pmWuJ5HWtx3z6',
    'Demo Artisan',
    'ACTIVE',
    true
  )
  ON CONFLICT (email) DO NOTHING;

  SELECT id INTO demo_user_id FROM identity.users WHERE email = 'demo.artisan@artisanplatform.dev';

  INSERT INTO identity.user_roles (user_id, role_id)
  SELECT demo_user_id, r.id FROM identity.roles r WHERE r.name = 'ARTISAN'
  ON CONFLICT DO NOTHING;

  INSERT INTO seller.sellers (user_id, seller_type, display_name, verification_status)
  VALUES (demo_user_id, 'ARTISAN', 'Demo Artisan Studio', 'VERIFIED')
  ON CONFLICT (user_id) DO NOTHING;

  SELECT id INTO demo_seller_id FROM seller.sellers WHERE user_id = demo_user_id;

  INSERT INTO catalog.products (seller_id, title, description, status)
  SELECT demo_seller_id, 'Hand-thrown Ceramic Bowl', 'A demo product seeded for local development.', 'ACTIVE'
  WHERE NOT EXISTS (
    SELECT 1 FROM catalog.products WHERE seller_id = demo_seller_id AND title = 'Hand-thrown Ceramic Bowl'
  );
END $$;
SQL

echo "==> Seed complete. Demo login: demo.artisan@artisanplatform.dev / Password123!"
