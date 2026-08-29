package com.artisanplatform.commerce.integration;

import com.artisanplatform.testsupport.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Full round-trip test for Order: HTTP request -> service -> PostgreSQL
 * (Testcontainers) -> response. Extends {@link BaseIntegrationTest} for the
 * shared container wiring. See docs/architecture/09_TESTING_STRATEGY.md §3.
 */
@AutoConfigureMockMvc
class OrderIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void fullRoundTrip_createThenGet_returnsPersistedEntity() {
        // TODO: once OrderController's create/get methods are implemented,
        // POST a valid OrderRequest, assert 2xx, then GET the created
        // resource and assert the response matches what was submitted.
    }
}
