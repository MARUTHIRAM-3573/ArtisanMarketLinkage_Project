package com.artisanplatform.auth.integration;

import com.artisanplatform.testsupport.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Full round-trip test for User: HTTP request -> service -> PostgreSQL
 * (Testcontainers) -> response. Extends {@link BaseIntegrationTest} for the
 * shared container wiring. See docs/architecture/09_TESTING_STRATEGY.md §3.
 */
@AutoConfigureMockMvc
class UserIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void fullRoundTrip_createThenGet_returnsPersistedEntity() {
        // TODO: once UserController's create/get methods are implemented,
        // POST a valid UserRequest, assert 2xx, then GET the created
        // resource and assert the response matches what was submitted.
    }
}
