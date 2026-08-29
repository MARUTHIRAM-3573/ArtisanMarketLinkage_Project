# Multi-stage build for the single Artisan Platform backend deployable.
# Build context is `backend/` (see docker-compose.yml: context: ./backend,
# dockerfile: app/Dockerfile) so this Dockerfile can COPY the whole Maven
# reactor — every domain module the `app` module depends on.

# ---------- Stage 1: build ----------
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /workspace

# Copy POM files first so Maven's dependency resolution layer is cached
# independently of source changes.
COPY pom.xml ./pom.xml
COPY common/pom.xml ./common/pom.xml
COPY test-support/pom.xml ./test-support/pom.xml
COPY modules/auth/pom.xml ./modules/auth/pom.xml
COPY modules/seller/pom.xml ./modules/seller/pom.xml
COPY modules/catalog/pom.xml ./modules/catalog/pom.xml
COPY modules/media/pom.xml ./modules/media/pom.xml
COPY modules/inventory/pom.xml ./modules/inventory/pom.xml
COPY modules/ai/pom.xml ./modules/ai/pom.xml
COPY modules/pricing/pom.xml ./modules/pricing/pom.xml
COPY modules/market/pom.xml ./modules/market/pom.xml
COPY modules/b2b/pom.xml ./modules/b2b/pom.xml
COPY modules/commerce/pom.xml ./modules/commerce/pom.xml
COPY modules/payment/pom.xml ./modules/payment/pom.xml
COPY app/pom.xml ./app/pom.xml
RUN mvn -B -q dependency:go-offline || true

# Now copy full source and build the single deployable JAR.
COPY common ./common
COPY test-support ./test-support
COPY modules ./modules
COPY app ./app
RUN mvn -B -q -pl app -am package -DskipTests

# ---------- Stage 2: runtime ----------
FROM eclipse-temurin:21-jre-alpine AS runtime

RUN addgroup -S artisan && adduser -S artisan -G artisan
WORKDIR /app

COPY --from=builder /workspace/app/target/artisan-platform.jar /app/artisan-platform.jar
# Flyway migrations live at the monorepo root (database/migrations/), not
# inside the backend/ build context — mounted at runtime via docker-compose's
# `./database/migrations:/flyway/migrations:ro` volume for local dev; for a
# real deployment, bake them into the image at this same path instead:
# COPY database/migrations /flyway/migrations
RUN mkdir -p /app/uploads /flyway/migrations && chown -R artisan:artisan /app /flyway

USER artisan
EXPOSE 8080

# Container-aware JVM flags per the scaffold brief.
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

HEALTHCHECK --interval=10s --timeout=5s --retries=10 --start-period=30s \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/artisan-platform.jar"]
