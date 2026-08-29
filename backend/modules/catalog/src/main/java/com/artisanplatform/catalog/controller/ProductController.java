package com.artisanplatform.catalog.controller;

import com.artisanplatform.catalog.domain.dto.request.ProductRequest;
import com.artisanplatform.catalog.domain.dto.response.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Catalog schema — categories, products, SKUs, attributes.
 * One method per endpoint defined for this module in docs/architecture/05_API_CONTRACTS.md.
 */
@RestController
@RequiredArgsConstructor
public class ProductController {

    private final com.artisanplatform.catalog.service.ProductService productService;

    @GetMapping("/api/v1/products")
    @Operation(summary = "Browse products (public)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    public org.springframework.http.ResponseEntity<com.artisanplatform.common.response.ApiResponse<ProductResponse>> listProducts() {
        // TODO: implement — wire to ProductService; this stub returns 501 until business logic lands.
        throw new UnsupportedOperationException("listProducts not yet implemented");
    }


    @PostMapping("/api/v1/products")
    @Operation(summary = "Create a product")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PreAuthorize("hasRole('ARTISAN')")
    public org.springframework.http.ResponseEntity<com.artisanplatform.common.response.ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request) {
        // TODO: implement — wire to ProductService; this stub returns 501 until business logic lands.
        throw new UnsupportedOperationException("createProduct not yet implemented");
    }


    @GetMapping("/api/v1/products/{id}")
    @Operation(summary = "Get product detail")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    public org.springframework.http.ResponseEntity<com.artisanplatform.common.response.ApiResponse<ProductResponse>> getProduct(@PathVariable UUID id) {
        // TODO: implement — wire to ProductService; this stub returns 501 until business logic lands.
        throw new UnsupportedOperationException("getProduct not yet implemented");
    }


    @PutMapping("/api/v1/products/{id}")
    @Operation(summary = "Update a product (owner only)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PreAuthorize("hasRole('ARTISAN')")
    public org.springframework.http.ResponseEntity<com.artisanplatform.common.response.ApiResponse<ProductResponse>> updateProduct(@PathVariable UUID id, @Valid @RequestBody ProductRequest request) {
        // TODO: implement — wire to ProductService; this stub returns 501 until business logic lands.
        throw new UnsupportedOperationException("updateProduct not yet implemented");
    }
}
