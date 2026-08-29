package com.artisanplatform.market.domain.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Response payload for a MarketListing. Field set matches docs/architecture/05_API_CONTRACTS.md. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketListingResponse {

    private UUID id;

    private UUID productId;

    private UUID marketChannelId;

    private String status;
}
