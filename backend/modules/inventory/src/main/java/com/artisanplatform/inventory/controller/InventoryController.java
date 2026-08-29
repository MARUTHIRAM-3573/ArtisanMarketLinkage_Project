package com.artisanplatform.inventory.controller;

import com.artisanplatform.inventory.domain.dto.request.InventoryRequest;
import com.artisanplatform.inventory.domain.dto.response.InventoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Inventory schema — stock levels and movement ledger.
 * One method per endpoint defined for this module in docs/architecture/05_API_CONTRACTS.md.
 */
@RestController
@RequiredArgsConstructor
public class InventoryController {

    private final com.artisanplatform.inventory.service.InventoryService inventoryService;

    @GetMapping("/api/v1/skus/{skuId}/inventory")
    @Operation(summary = "Get current stock for a SKU")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PreAuthorize("isAuthenticated()")
    public org.springframework.http.ResponseEntity<com.artisanplatform.common.response.ApiResponse<InventoryResponse>> getInventory(@PathVariable UUID skuId) {
        // TODO: implement — wire to InventoryService; this stub returns 501 until business logic lands.
        throw new UnsupportedOperationException("getInventory not yet implemented");
    }


    @PostMapping("/api/v1/skus/{skuId}/inventory/movements")
    @Operation(summary = "Record a stock movement")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PreAuthorize("hasRole('ARTISAN')")
    public org.springframework.http.ResponseEntity<com.artisanplatform.common.response.ApiResponse<InventoryResponse>> recordMovement(@PathVariable UUID skuId, @Valid @RequestBody InventoryRequest request) {
        // TODO: implement — wire to InventoryService; this stub returns 501 until business logic lands.
        throw new UnsupportedOperationException("recordMovement not yet implemented");
    }
}
