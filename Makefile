.PHONY: install dev build test lint format docker-up docker-down docker-clean migrate seed logs health

install: ## Install backend (Maven deps) and frontend (npm deps)
	mvn -f backend/pom.xml install -DskipTests
	npm install --prefix frontend

dev: ## Run backend (local profile) and frontend (Expo) for local development
	@echo "Run these in two separate terminals:"
	@echo "  1) mvn -f backend/app/pom.xml spring-boot:run -Dspring-boot.run.profiles=local"
	@echo "  2) cd frontend && npx expo start"

build: ## Build backend JAR and frontend bundle
	mvn -f backend/pom.xml package -DskipTests
	npm run build --prefix frontend

test: ## Run backend (unit+integration via Testcontainers) and frontend tests
	mvn -f backend/pom.xml verify
	npm test --prefix frontend

lint: ## Lint backend (Checkstyle) and frontend (ESLint)
	mvn -f backend/pom.xml checkstyle:check
	npm run lint --prefix frontend

format: ## Auto-format frontend code (Prettier) — backend formatting is enforced via Checkstyle, not auto-fixed
	npm run format --prefix frontend

docker-up: ## Start the local stack (PostgreSQL + backend)
	docker compose up -d --build

docker-down: ## Stop the local stack, keep volumes
	docker compose down

docker-clean: ## Stop the local stack and remove volumes (DESTROYS local DB data)
	docker compose down -v

migrate: ## Apply Flyway migrations against the running PostgreSQL container
	mvn -f backend/app/pom.xml flyway:migrate \
		-Dflyway.url=jdbc:postgresql://localhost:5432/artisan_marketplace \
		-Dflyway.user=$${DB_USERNAME:-artisan} \
		-Dflyway.password=$${DB_PASSWORD:-artisan_dev_password} \
		-Dflyway.locations=filesystem:database/migrations

seed: ## Seed reference data (roles, market channels) — see infra/scripts/seed.sh
	bash infra/scripts/seed.sh

logs: ## Tail logs from the local stack
	docker compose logs -f backend

health: ## Check health of all local services
	bash infra/scripts/health-check.sh
