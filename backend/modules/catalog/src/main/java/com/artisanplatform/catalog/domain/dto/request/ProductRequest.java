package com.artisanplatform.catalog.domain.dto.request;

import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request payload for creating/updating a Product. Field set matches docs/architecture/05_API_CONTRACTS.md. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotNull
    private UUID sellerId;

    private UUID categoryId;

    @NotBlank
    private String title;

    private String description;

    @NotBlank
    private String status;

    private UUID sourceCatalogGenerationId;
}
