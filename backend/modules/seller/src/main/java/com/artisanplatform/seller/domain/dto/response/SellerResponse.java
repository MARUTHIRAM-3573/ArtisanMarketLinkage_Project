package com.artisanplatform.seller.domain.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Response payload for a Seller. Field set matches docs/architecture/05_API_CONTRACTS.md. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerResponse {

    private UUID id;

    private UUID userId;

    private String sellerType;

    private String displayName;

    private String verificationStatus;
}
