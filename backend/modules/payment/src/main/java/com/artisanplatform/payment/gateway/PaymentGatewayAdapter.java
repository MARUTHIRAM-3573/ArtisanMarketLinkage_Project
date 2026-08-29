package com.artisanplatform.payment.gateway;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Provider-agnostic payment gateway interface (principle #14). The MVP uses
 * {@link MockPaymentGatewayAdapter} exclusively (source §22: "a mock payment
 * gateway can be used" for the hackathon MVP) — a real gateway integration
 * is deferred (docs/architecture/01_PRODUCT_SCOPE.md §8.2). Never add a
 * card number, CVV, or full payment credential field to any type in this
 * package (principle #22/#40).
 */
public interface PaymentGatewayAdapter {

    ChargeResult charge(UUID orderId, BigDecimal amount);

    RefundResult refund(String gatewayTransactionId, BigDecimal amount);

    record ChargeResult(boolean success, String gatewayTransactionId, String gatewayReference) {
    }

    record RefundResult(boolean success, String gatewayRefundId) {
    }
}
