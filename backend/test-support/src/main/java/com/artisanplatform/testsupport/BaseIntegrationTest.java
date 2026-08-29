package com.artisanplatform.testsupport;

import com.artisanplatform.testsupport.containers.PostgresTestContainer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Abstract base class every module's {@code *IntegrationTest} extends.
 * Wires the shared Postgres Testcontainer's JDBC URL/credentials into
 * Spring's environment so {@code @SpringBootTest} boots against the real
 * container instead of an embedded/mocked database.
 *
 * <p>Concrete subclasses add {@code @AutoConfigureMockMvc} and whatever
 * module-specific {@code @Import}/{@code @ActiveProfiles} they need.
 */
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegrationTest {

    @DynamicPropertySource
    static void registerPostgresProperties(DynamicPropertyRegistry registry) {
        var postgres = PostgresTestContainer.getInstance();
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.locations", () -> "filesystem:../../../database/migrations");
    }
}
