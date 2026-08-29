package com.artisanplatform.commerce.controller;

import com.artisanplatform.commerce.domain.dto.request.OrderRequest;
import com.artisanplatform.commerce.domain.dto.response.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Commerce schema — the ONE shared order pipeline for B2C/B2B/GOVERNMENT (principle #11).
 * One method per endpoint defined for this module in docs/architecture/05_API_CONTRACTS.md.
 */
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final com.artisanplatform.commerce.service.OrderService orderService;

    @GetMapping("/api/v1/orders")
    @Operation(summary = "List the caller's orders")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PreAuthorize("isAuthenticated()")
    public org.springframework.http.ResponseEntity<com.artisanplatform.common.response.ApiResponse<OrderResponse>> listOrders() {
        // TODO: implement — wire to OrderService; this stub returns 501 until business logic lands.
        throw new UnsupportedOperationException("listOrders not yet implemented");
    }


    @GetMapping("/api/v1/orders/{id}")
    @Operation(summary = "Get order detail + status history")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PreAuthorize("isAuthenticated()")
    public org.springframework.http.ResponseEntity<com.artisanplatform.common.response.ApiResponse<OrderResponse>> getOrder(@PathVariable UUID id) {
        // TODO: implement — wire to OrderService; this stub returns 501 until business logic lands.
        throw new UnsupportedOperationException("getOrder not yet implemented");
    }


    @PostMapping("/api/v1/checkout")
    @Operation(summary = "Check out the current cart into an order")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PreAuthorize("isAuthenticated()")
    public org.springframework.http.ResponseEntity<com.artisanplatform.common.response.ApiResponse<OrderResponse>> checkout() {
        // TODO: implement — wire to OrderService; this stub returns 501 until business logic lands.
        throw new UnsupportedOperationException("checkout not yet implemented");
    }
}
