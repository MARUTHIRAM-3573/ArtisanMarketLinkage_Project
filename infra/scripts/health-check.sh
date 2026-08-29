#!/usr/bin/env bash
set -euo pipefail

# infra/scripts/health-check.sh
#
# Checks that every service in the local stack (docker-compose.yml) reports
# healthy. Used by `make health` and by bootstrap.sh (with --wait, which
# polls instead of checking once).
#
# Exit code 0 = all healthy, 1 = something isn't.

WAIT=false
if [ "${1:-}" = "--wait" ]; then
  WAIT=true
fi

MAX_ATTEMPTS=30
SLEEP_SECONDS=5

check_once() {
  local all_healthy=true

  local pg_status
  pg_status="$(docker inspect --format '{{.State.Health.Status}}' artisan-postgres 2>/dev/null || echo "missing")"
  echo "postgres: ${pg_status}"
  [ "$pg_status" = "healthy" ] || all_healthy=false

  local backend_status
  backend_status="$(docker inspect --format '{{.State.Health.Status}}' artisan-backend 2>/dev/null || echo "missing")"
  echo "backend:  ${backend_status}"
  [ "$backend_status" = "healthy" ] || all_healthy=false

  $all_healthy
}

if [ "$WAIT" = true ]; then
  echo "==> Waiting for all services to become healthy (up to $((MAX_ATTEMPTS * SLEEP_SECONDS))s)"
  for attempt in $(seq 1 "$MAX_ATTEMPTS"); do
    if check_once; then
      echo "==> All services healthy"
      exit 0
    fi
    echo "    (attempt ${attempt}/${MAX_ATTEMPTS}, retrying in ${SLEEP_SECONDS}s)"
    sleep "$SLEEP_SECONDS"
  done
  echo "==> Timed out waiting for services to become healthy" >&2
  exit 1
else
  if check_once; then
    echo "==> All services healthy"
    exit 0
  else
    echo "==> One or more services are not healthy" >&2
    exit 1
  fi
fi
