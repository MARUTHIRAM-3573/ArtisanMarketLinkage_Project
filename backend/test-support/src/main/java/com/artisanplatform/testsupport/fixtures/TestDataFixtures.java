package com.artisanplatform.testsupport.fixtures;

import java.util.UUID;

/**
 * Shared test-data builders so unit/integration tests across modules don't
 * each hand-roll fixture values. Extend with module-specific builders as
 * entities are implemented (e.g. {@code aProduct()}, {@code aSeller()}).
 */
public final class TestDataFixtures {

    private TestDataFixtures() {
    }

    public static String aRandomEmail() {
        return "test-" + UUID.randomUUID() + "@example.test";
    }

    public static String aStrongPassword() {
        return "Test-Password-123!";
    }
}
