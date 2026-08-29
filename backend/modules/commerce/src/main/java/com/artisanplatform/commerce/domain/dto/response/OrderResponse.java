package com.artisanplatform.commerce.domain.dto.response;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Response payload for a Order. Field set matches docs/architecture/05_API_CONTRACTS.md. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private UUID id;

    private UUID userId;

    private String sourceType;

    private UUID sourceReferenceId;

    private String status;

    private BigDecimal totalAmount;

    private UUID shippingAddressId;
}
