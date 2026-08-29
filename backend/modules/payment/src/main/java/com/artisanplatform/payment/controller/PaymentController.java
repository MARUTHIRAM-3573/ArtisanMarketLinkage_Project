package com.artisanplatform.payment.controller;

import com.artisanplatform.payment.domain.dto.request.PaymentRequest;
import com.artisanplatform.payment.domain.dto.response.PaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Payment schema — payments, transactions, refunds, settlements, invoices. NEVER stores card/CVV data (principle #22/#40).
 * One method per endpoint defined for this module in docs/architecture/05_API_CONTRACTS.md.
 */
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final com.artisanplatform.payment.service.PaymentService paymentService;

    @PostMapping("/api/v1/orders/{id}/payments")
    @Operation(summary = "Initiate payment for an order (mock gateway)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PreAuthorize("isAuthenticated()")
    public org.springframework.http.ResponseEntity<com.artisanplatform.common.response.ApiResponse<PaymentResponse>> initiatePayment(@PathVariable UUID id, @Valid @RequestBody PaymentRequest request) {
        // TODO: implement — wire to PaymentService; this stub returns 501 until business logic lands.
        throw new UnsupportedOperationException("initiatePayment not yet implemented");
    }


    @GetMapping("/api/v1/payments/{id}")
    @Operation(summary = "Get payment detail + transactions")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PreAuthorize("isAuthenticated()")
    public org.springframework.http.ResponseEntity<com.artisanplatform.common.response.ApiResponse<PaymentResponse>> getPayment(@PathVariable UUID id) {
        // TODO: implement — wire to PaymentService; this stub returns 501 until business logic lands.
        throw new UnsupportedOperationException("getPayment not yet implemented");
    }


    @PostMapping("/api/v1/payments/{id}/refunds")
    @Operation(summary = "Issue a refund")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('ARTISAN')")
    public org.springframework.http.ResponseEntity<com.artisanplatform.common.response.ApiResponse<PaymentResponse>> issueRefund(@PathVariable UUID id, @Valid @RequestBody PaymentRequest request) {
        // TODO: implement — wire to PaymentService; this stub returns 501 until business logic lands.
        throw new UnsupportedOperationException("issueRefund not yet implemented");
    }
}
