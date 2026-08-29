package com.artisanplatform.common.constants;

import java.util.List;

/**
 * Cross-module constants derived from the fixed reference-data enumerations
 * in docs/architecture/04_DATA_MODEL_AND_OWNERSHIP.md. Keeping these in one
 * shared place (rather than re-declaring string literals per module) is
 * what keeps role/channel checks consistent across every controller.
 */
public final class GlobalConstants {

    private GlobalConstants() {
    }

    public static final class Roles {
        private Roles() {
        }

        public static final String ADMIN = "ADMIN";
        public static final String ARTISAN = "ARTISAN";
        public static final String CUSTOMER = "CUSTOMER";
        public static final String B2B_BUYER = "B2B_BUYER";

        public static final List<String> ALL = List.of(ADMIN, ARTISAN, CUSTOMER, B2B_BUYER);
    }

    public static final class MarketChannels {
        private MarketChannels() {
        }

        public static final String B2C = "B2C";
        public static final String B2B = "B2B";
        public static final String GOVERNMENT = "GOVERNMENT";
    }

    public static final class OrderStatus {
        private OrderStatus() {
        }

        public static final String PENDING = "PENDING";
        public static final String CONFIRMED = "CONFIRMED";
        public static final String PROCESSING = "PROCESSING";
        public static final String SHIPPED = "SHIPPED";
        public static final String DELIVERED = "DELIVERED";
        // NOTE: no CANCELLED/RETURNED state exists in the source enum — see
        // docs/architecture/04_DATA_MODEL_AND_OWNERSHIP.md open question.
    }

    public static final String API_BASE_PATH = "/api/v1";
}
