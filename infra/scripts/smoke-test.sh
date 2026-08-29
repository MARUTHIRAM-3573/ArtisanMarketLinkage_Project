#!/usr/bin/env bash
set -euo pipefail

# infra/scripts/smoke-test.sh
#
# Minimal end-to-end check against a running local stack: actuator health,
# login with the seed.sh demo user, and one authenticated GET. This is not
# a substitute for the backend's own integration test suite
# (backend/**/src/test) — it's a fast "did `make docker-up` actually work"
# check for a developer (or CI) right after bootstrap.sh.

BASE_URL="${BASE_URL:-http://localhost:8080/api/v1}"
DEMO_EMAIL="demo.artisan@artisanplatform.dev"
DEMO_PASSWORD="Password123!"

fail() {
  echo "SMOKE TEST FAILED: $1" >&2
  exit 1
}

echo "==> 1/3 Checking actuator health"
health_status="$(curl -s -o /dev/null -w '%{http_code}' "http://localhost:8080/actuator/health" || true)"
[ "$health_status" = "200" ] || fail "actuator/health returned ${health_status}"
echo "    OK"

echo "==> 2/3 Logging in as demo user (requires infra/scripts/seed.sh to have run)"
login_response="$(curl -s -X POST "${BASE_URL}/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"${DEMO_EMAIL}\",\"password\":\"${DEMO_PASSWORD}\"}")"

access_token="$(echo "$login_response" | python3 -c 'import json,sys; print(json.load(sys.stdin)["data"]["accessToken"])' 2>/dev/null || true)"
[ -n "$access_token" ] || fail "login did not return an access token. Response: ${login_response}"
echo "    OK"

echo "==> 3/3 Fetching product catalog with the access token"
products_status="$(curl -s -o /dev/null -w '%{http_code}' "${BASE_URL}/products" \
  -H "Authorization: Bearer ${access_token}")"
[ "$products_status" = "200" ] || fail "GET /products returned ${products_status}"
echo "    OK"

echo "==> Smoke test passed"
