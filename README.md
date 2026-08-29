# Artisan Digital Commerce Platform

An AI-enabled digital commerce platform that helps artisans with limited digital literacy digitize their products — via voice input, AI catalog generation, AI image enhancement, and AI pricing recommendations — and sell them through three channels: B2C (direct to consumers), B2B (inquiry/quotation/purchase-order), and Government/institutional procurement.

## Tech Stack

| Layer | Technology |
|---|---|
| Mobile frontend | React Native + TypeScript + Expo (Android-first) |
| Backend | Java 21 + Spring Boot + Spring Security + Maven (multi-module, single deployable) |
| Database | PostgreSQL (`artisan_marketplace`), Flyway migrations |
| AI integration | Backend-owned, provider-agnostic adapters (STT, translation, catalog generation, image enhancement, pricing) |
| Media storage | Local filesystem (MVP), abstracted behind `MediaStorageService` for future S3 migration |
| CI/CD | GitHub Actions |

See `docs/architecture/02_ARCHITECTURE_OVERVIEW.md` for the full architecture decision record, and `docs/architecture/16_ADR_PACK.md` for every technology choice with justification. **This is a modular monolith, not microservices** — one Spring Boot deployable internally organized into 11 domain modules.

## Repository Structure

See `REPOSITORY_STRUCTURE.md` at the repository root for the full annotated tree. In short:

- `backend/` — Spring Boot multi-module Maven project (`common/`, `test-support/`, `modules/<domain>/` × 11, `app/` — the one runnable application)
- `frontend/` — React Native + Expo mobile app
- `database/migrations/` — Flyway SQL migrations (shared across the whole backend)
- `infra/scripts/` — bootstrap/seed/health-check/smoke-test scripts
- `docs/` — architecture documents, OpenAPI spec, onboarding guide

## Prerequisites

- Java 21 (Temurin/OpenJDK)
- Maven 3.9+
- Node.js 20+ and npm
- Docker + Docker Compose
- Expo CLI (`npm install -g expo-cli` or use `npx expo`)
- Android SDK + emulator, or a physical Android device (Android Studio optional)

## How to Run Locally

Full step-by-step instructions: [`docs/onboarding.md`](docs/onboarding.md).

Quick start:

```bash
cp .env.example .env.local
make docker-up      # starts PostgreSQL + backend
make migrate         # applies Flyway migrations (auto-runs on backend startup too)
make seed            # seeds reference data (roles, market channels)
cd frontend && npx expo start
```

## How to Run Tests

```bash
make test            # backend (mvn verify across all modules) + frontend (vitest/jest)
```

## Key Planning Documents

| Document | What it covers |
|---|---|
| [`docs/architecture/01_PRODUCT_SCOPE.md`](docs/architecture/01_PRODUCT_SCOPE.md) | Vision, personas, requirements, MVP boundary |
| [`docs/architecture/02_ARCHITECTURE_OVERVIEW.md`](docs/architecture/02_ARCHITECTURE_OVERVIEW.md) | System architecture, tech stack rationale |
| [`docs/architecture/03_SERVICE_BOUNDARIES.md`](docs/architecture/03_SERVICE_BOUNDARIES.md) | Module catalogue and dependency graph |
| [`docs/architecture/04_DATA_MODEL_AND_OWNERSHIP.md`](docs/architecture/04_DATA_MODEL_AND_OWNERSHIP.md) | Full data model, ERD, migration strategy |
| [`docs/architecture/05_API_CONTRACTS.md`](docs/architecture/05_API_CONTRACTS.md) | Endpoint catalogue |
| [`docs/architecture/CODEX_MEMORY.md`](docs/architecture/CODEX_MEMORY.md) | Compressed context for AI coding agents — **read this first** |
| [`docs/architecture/DOCUMENT_INDEX.md`](docs/architecture/DOCUMENT_INDEX.md) | Full index of all 20 planning documents |

## Contributing

See `docs/onboarding.md` for first-contribution steps. Branch naming: `feature/<name>` / `fix/<name>`, merged into `main` via Pull Request (no direct commits to `main`).
