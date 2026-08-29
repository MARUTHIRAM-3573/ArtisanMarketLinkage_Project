package com.artisanplatform.inventory.domain.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Response payload for a Inventory. Field set matches docs/architecture/05_API_CONTRACTS.md. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {

    private UUID id;

    private UUID productSkuId;

    private Integer availableQuantity;

    private Integer reservedQuantity;

    private Integer reorderLevel;
}
