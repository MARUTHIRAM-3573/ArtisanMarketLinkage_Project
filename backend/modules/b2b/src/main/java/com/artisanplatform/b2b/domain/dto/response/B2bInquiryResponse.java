package com.artisanplatform.b2b.domain.dto.response;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Response payload for a B2bInquiry. Field set matches docs/architecture/05_API_CONTRACTS.md. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class B2bInquiryResponse {

    private UUID id;

    private UUID b2bBuyerId;

    private UUID sellerId;

    private UUID productId;

    private Integer requestedQuantity;

    private BigDecimal targetPrice;

    private String message;

    private String deliveryRequirement;

    private String status;
}
