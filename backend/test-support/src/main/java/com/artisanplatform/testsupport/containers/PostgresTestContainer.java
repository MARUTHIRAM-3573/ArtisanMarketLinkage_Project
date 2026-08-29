package com.artisanplatform.testsupport.containers;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Single shared PostgreSQL Testcontainer definition. Every module's
 * {@code *IntegrationTest} runs Flyway migrations from database/migrations
 * against this container, so integration tests exercise the real schema,
 * not a mocked one — per docs/architecture/09_TESTING_STRATEGY.md §4.
 *
 * <p>Using a static singleton container (started once per test JVM) keeps
 * the full multi-module test suite fast; each test class is still
 * responsible for cleaning up the data it creates, or wrapping tests in a
 * rolled-back transaction.
 */
public final class PostgresTestContainer {

    private static final PostgreSQLContainer<?> INSTANCE =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
                    .withDatabaseName("artisan_marketplace_test")
                    .withUsername("artisan_test")
                    .withPassword("artisan_test");

    private PostgresTestContainer() {
    }

    public static PostgreSQLContainer<?> getInstance() {
        if (!INSTANCE.isRunning()) {
            INSTANCE.start();
        }
        return INSTANCE;
    }
}
