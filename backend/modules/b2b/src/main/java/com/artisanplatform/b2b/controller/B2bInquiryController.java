package com.artisanplatform.b2b.controller;

import com.artisanplatform.b2b.domain.dto.request.B2bInquiryRequest;
import com.artisanplatform.b2b.domain.dto.response.B2bInquiryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * B2B schema — buyers, inquiries, quotations, purchase orders.
 * One method per endpoint defined for this module in docs/architecture/05_API_CONTRACTS.md.
 */
@RestController
@RequiredArgsConstructor
public class B2bInquiryController {

    private final com.artisanplatform.b2b.service.B2bInquiryService b2bInquiryService;

    @PostMapping("/api/v1/b2b/inquiries")
    @Operation(summary = "Submit a B2B inquiry")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PreAuthorize("hasRole('B2B_BUYER')")
    public org.springframework.http.ResponseEntity<com.artisanplatform.common.response.ApiResponse<B2bInquiryResponse>> createInquiry(@Valid @RequestBody B2bInquiryRequest request) {
        // TODO: implement — wire to B2bInquiryService; this stub returns 501 until business logic lands.
        throw new UnsupportedOperationException("createInquiry not yet implemented");
    }


    @GetMapping("/api/v1/b2b/inquiries/{id}")
    @Operation(summary = "Get inquiry detail")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PreAuthorize("isAuthenticated()")
    public org.springframework.http.ResponseEntity<com.artisanplatform.common.response.ApiResponse<B2bInquiryResponse>> getInquiry(@PathVariable UUID id) {
        // TODO: implement — wire to B2bInquiryService; this stub returns 501 until business logic lands.
        throw new UnsupportedOperationException("getInquiry not yet implemented");
    }
}
