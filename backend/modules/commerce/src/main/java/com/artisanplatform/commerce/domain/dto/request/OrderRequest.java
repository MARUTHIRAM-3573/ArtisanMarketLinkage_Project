package com.artisanplatform.commerce.domain.dto.request;

import java.math.BigDecimal;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request payload for creating/updating a Order. Field set matches docs/architecture/05_API_CONTRACTS.md. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    @NotNull
    private UUID userId;

    @NotBlank
    private String sourceType;

    private UUID sourceReferenceId;

    @NotBlank
    private String status;

    @NotNull
    private BigDecimal totalAmount;

    @NotNull
    private UUID shippingAddressId;
}
