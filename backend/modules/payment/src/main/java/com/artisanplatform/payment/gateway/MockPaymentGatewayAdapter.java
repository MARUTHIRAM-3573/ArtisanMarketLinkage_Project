package com.artisanplatform.payment.gateway;

import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Mock payment gateway — always succeeds, generates a fake transaction id.
 * This is the ONLY payment gateway implementation for the MVP (source §22).
 * Swap in a real implementation behind {@link PaymentGatewayAdapter} when a
 * real gateway is integrated; no other module needs to change.
 */
@Component
public class MockPaymentGatewayAdapter implements PaymentGatewayAdapter {

    private final String mode;

    public MockPaymentGatewayAdapter(@Value("${app.payment.gateway-mode:MOCK}") String mode) {
        this.mode = mode;
    }

    @Override
    public ChargeResult charge(UUID orderId, BigDecimal amount) {
        if (!"MOCK".equals(mode)) {
            throw new UnsupportedOperationException(
                    "Only MOCK payment gateway mode is implemented for MVP — see docs/architecture/01_PRODUCT_SCOPE.md §8.2");
        }
        String fakeTransactionId = "MOCK-TXN-" + UUID.randomUUID();
        return new ChargeResult(true, fakeTransactionId, "mock-gateway-ref-" + orderId);
    }

    @Override
    public RefundResult refund(String gatewayTransactionId, BigDecimal amount) {
        return new RefundResult(true, "MOCK-REFUND-" + UUID.randomUUID());
    }
}
