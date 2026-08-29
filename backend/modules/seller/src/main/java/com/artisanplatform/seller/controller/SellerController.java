package com.artisanplatform.seller.controller;

import com.artisanplatform.seller.domain.dto.request.SellerRequest;
import com.artisanplatform.seller.domain.dto.response.SellerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Seller schema — seller accounts and artisan profiles.
 * One method per endpoint defined for this module in docs/architecture/05_API_CONTRACTS.md.
 */
@RestController
@RequiredArgsConstructor
public class SellerController {

    private final com.artisanplatform.seller.service.SellerService sellerService;

    @PostMapping("/api/v1/sellers")
    @Operation(summary = "Register a seller account")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PreAuthorize("hasRole('ARTISAN')")
    public org.springframework.http.ResponseEntity<com.artisanplatform.common.response.ApiResponse<SellerResponse>> createSeller(@Valid @RequestBody SellerRequest request) {
        // TODO: implement — wire to SellerService; this stub returns 501 until business logic lands.
        throw new UnsupportedOperationException("createSeller not yet implemented");
    }


    @GetMapping("/api/v1/sellers/{id}")
    @Operation(summary = "Get a seller profile")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PreAuthorize("isAuthenticated()")
    public org.springframework.http.ResponseEntity<com.artisanplatform.common.response.ApiResponse<SellerResponse>> getSeller(@PathVariable UUID id) {
        // TODO: implement — wire to SellerService; this stub returns 501 until business logic lands.
        throw new UnsupportedOperationException("getSeller not yet implemented");
    }
}
