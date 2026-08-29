package com.artisanplatform.market.domain.dto.request;

import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request payload for creating/updating a MarketListing. Field set matches docs/architecture/05_API_CONTRACTS.md. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketListingRequest {

    @NotNull
    private UUID productId;

    @NotNull
    private UUID marketChannelId;

    @NotBlank
    private String status;
}
