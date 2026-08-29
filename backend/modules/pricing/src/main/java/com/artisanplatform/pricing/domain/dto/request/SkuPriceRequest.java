package com.artisanplatform.pricing.domain.dto.request;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request payload for creating/updating a SkuPrice. Field set matches docs/architecture/05_API_CONTRACTS.md. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkuPriceRequest {

    @NotNull
    private UUID productSkuId;

    @NotBlank
    private String priceType;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private Instant validFrom;

    private Instant validTo;
}
