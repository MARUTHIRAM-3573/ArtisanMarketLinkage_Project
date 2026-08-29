package com.artisanplatform.pricing.controller;

import com.artisanplatform.pricing.domain.dto.request.SkuPriceRequest;
import com.artisanplatform.pricing.domain.dto.response.SkuPriceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Pricing schema — cost records, market prices, SKU prices.
 * One method per endpoint defined for this module in docs/architecture/05_API_CONTRACTS.md.
 */
@RestController
@RequiredArgsConstructor
public class SkuPriceController {

    private final com.artisanplatform.pricing.service.SkuPriceService skuPriceService;

    @GetMapping("/api/v1/skus/{skuId}/prices")
    @Operation(summary = "List price history for a SKU")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    public org.springframework.http.ResponseEntity<com.artisanplatform.common.response.ApiResponse<SkuPriceResponse>> listPrices(@PathVariable UUID skuId) {
        // TODO: implement — wire to SkuPriceService; this stub returns 501 until business logic lands.
        throw new UnsupportedOperationException("listPrices not yet implemented");
    }


    @PostMapping("/api/v1/skus/{skuId}/prices")
    @Operation(summary = "Set a SKU price (owner only)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PreAuthorize("hasRole('ARTISAN')")
    public org.springframework.http.ResponseEntity<com.artisanplatform.common.response.ApiResponse<SkuPriceResponse>> createPrice(@PathVariable UUID skuId, @Valid @RequestBody SkuPriceRequest request) {
        // TODO: implement — wire to SkuPriceService; this stub returns 501 until business logic lands.
        throw new UnsupportedOperationException("createPrice not yet implemented");
    }
}
