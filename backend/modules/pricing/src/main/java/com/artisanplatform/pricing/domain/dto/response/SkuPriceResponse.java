package com.artisanplatform.pricing.domain.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Response payload for a SkuPrice. Field set matches docs/architecture/05_API_CONTRACTS.md. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkuPriceResponse {

    private UUID id;

    private UUID productSkuId;

    private String priceType;

    private BigDecimal amount;

    private Instant validFrom;

    private Instant validTo;
}
