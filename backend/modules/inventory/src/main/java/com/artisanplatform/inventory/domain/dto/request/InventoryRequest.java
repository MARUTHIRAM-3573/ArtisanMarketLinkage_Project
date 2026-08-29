package com.artisanplatform.inventory.domain.dto.request;

import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request payload for creating/updating a Inventory. Field set matches docs/architecture/05_API_CONTRACTS.md. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequest {

    @NotNull
    private UUID productSkuId;

    @NotNull
    private Integer availableQuantity;

    @NotNull
    private Integer reservedQuantity;

    @NotNull
    private Integer reorderLevel;
}
