package com.artisanplatform.seller.domain.dto.request;

import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request payload for creating/updating a Seller. Field set matches docs/architecture/05_API_CONTRACTS.md. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerRequest {

    @NotNull
    private UUID userId;

    @NotBlank
    private String sellerType;

    @NotBlank
    private String displayName;

    @NotBlank
    private String verificationStatus;
}
