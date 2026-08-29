package com.artisanplatform.b2b.domain.dto.request;

import java.math.BigDecimal;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request payload for creating/updating a B2bInquiry. Field set matches docs/architecture/05_API_CONTRACTS.md. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class B2bInquiryRequest {

    @NotNull
    private UUID b2bBuyerId;

    @NotNull
    private UUID sellerId;

    @NotNull
    private UUID productId;

    @NotNull
    private Integer requestedQuantity;

    private BigDecimal targetPrice;

    private String message;

    private String deliveryRequirement;

    @NotBlank
    private String status;
}
