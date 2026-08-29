package com.artisanplatform.market.controller;

import com.artisanplatform.market.domain.dto.request.MarketListingRequest;
import com.artisanplatform.market.domain.dto.response.MarketListingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Market schema — channels, listings, external marketplace integration surface.
 * One method per endpoint defined for this module in docs/architecture/05_API_CONTRACTS.md.
 */
@RestController
@RequiredArgsConstructor
public class MarketListingController {

    private final com.artisanplatform.market.service.MarketListingService marketListingService;

    @GetMapping("/api/v1/products/{id}/listings")
    @Operation(summary = "List a product's channel listings")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    public org.springframework.http.ResponseEntity<com.artisanplatform.common.response.ApiResponse<MarketListingResponse>> listListings(@PathVariable UUID id) {
        // TODO: implement — wire to MarketListingService; this stub returns 501 until business logic lands.
        throw new UnsupportedOperationException("listListings not yet implemented");
    }


    @PostMapping("/api/v1/products/{id}/listings")
    @Operation(summary = "List a product on a channel")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PreAuthorize("hasRole('ARTISAN')")
    public org.springframework.http.ResponseEntity<com.artisanplatform.common.response.ApiResponse<MarketListingResponse>> createListing(@PathVariable UUID id, @Valid @RequestBody MarketListingRequest request) {
        // TODO: implement — wire to MarketListingService; this stub returns 501 until business logic lands.
        throw new UnsupportedOperationException("createListing not yet implemented");
    }
}
