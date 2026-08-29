#!/usr/bin/env bash
set -euo pipefail

# infra/scripts/bootstrap.sh
#
# One-shot "zero to running" setup for a fresh clone: copies .env.example,
# starts the local stack (PostgreSQL + backend, per docker-compose.yml),
# waits for both health checks to pass, then seeds reference data.
#
# This is the script referenced as the fast path in docs/onboarding.md.

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$REPO_ROOT"

echo "==> Artisan Platform bootstrap"

if [ ! -f .env.local ]; then
  echo "==> Creating .env.local from .env.example"
  cp .env.example .env.local
else
  echo "==> .env.local already exists, leaving it as-is"
fi

echo "==> Building and starting the local stack (postgres + backend)"
docker compose up -d --build

echo "==> Waiting for services to become healthy"
bash "$REPO_ROOT/infra/scripts/health-check.sh" --wait

echo "==> Seeding reference data"
bash "$REPO_ROOT/infra/scripts/seed.sh"

cat <<'EOF'

==> Backend is up: http://localhost:8080/api/v1
==> Swagger UI:     http://localhost:8080/swagger-ui.html
==> Postgres:       localhost:5432 (db: artisan_marketplace)

Next steps:
  cd frontend && npm install && npx expo start
See docs/onboarding.md for the full walkthrough.
EOF
