package com.artisanplatform.catalog.domain.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Response payload for a Product. Field set matches docs/architecture/05_API_CONTRACTS.md. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private UUID id;

    private UUID sellerId;

    private UUID categoryId;

    private String title;

    private String description;

    private String status;

    private UUID sourceCatalogGenerationId;
}
