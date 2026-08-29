package com.artisanplatform.payment.domain.dto.response;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Response payload for a Payment. Field set matches docs/architecture/05_API_CONTRACTS.md. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private UUID id;

    private UUID orderId;

    private BigDecimal amount;

    private String status;

    private String gatewayReference;
}
