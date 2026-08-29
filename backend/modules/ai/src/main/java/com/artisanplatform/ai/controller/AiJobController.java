package com.artisanplatform.ai.controller;

import com.artisanplatform.ai.domain.dto.request.AiJobRequest;
import com.artisanplatform.ai.domain.dto.response.AiJobResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * AI schema — job orchestration + every AI intermediate/result record. AI NEVER writes directly to catalog/pricing core tables (principle #9) — only the approval endpoints in catalog/pricing modules may do that.
 * One method per endpoint defined for this module in docs/architecture/05_API_CONTRACTS.md.
 */
@RestController
@RequiredArgsConstructor
public class AiJobController {

    private final com.artisanplatform.ai.service.AiJobService aiJobService;

    @PostMapping("/api/v1/ai/catalog/generate")
    @Operation(summary = "Submit an AI catalog-generation job")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PreAuthorize("hasRole('ARTISAN')")
    public org.springframework.http.ResponseEntity<com.artisanplatform.common.response.ApiResponse<AiJobResponse>> generateCatalog(@Valid @RequestBody AiJobRequest request) {
        // TODO: implement — wire to AiJobService; this stub returns 501 until business logic lands.
        throw new UnsupportedOperationException("generateCatalog not yet implemented");
    }


    @PostMapping("/api/v1/ai/image/enhance")
    @Operation(summary = "Submit an AI image-enhancement job")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PreAuthorize("hasRole('ARTISAN')")
    public org.springframework.http.ResponseEntity<com.artisanplatform.common.response.ApiResponse<AiJobResponse>> enhanceImage(@Valid @RequestBody AiJobRequest request) {
        // TODO: implement — wire to AiJobService; this stub returns 501 until business logic lands.
        throw new UnsupportedOperationException("enhanceImage not yet implemented");
    }


    @PostMapping("/api/v1/ai/pricing/recommend")
    @Operation(summary = "Submit an AI price-recommendation job")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PreAuthorize("hasRole('ARTISAN')")
    public org.springframework.http.ResponseEntity<com.artisanplatform.common.response.ApiResponse<AiJobResponse>> recommendPrice(@Valid @RequestBody AiJobRequest request) {
        // TODO: implement — wire to AiJobService; this stub returns 501 until business logic lands.
        throw new UnsupportedOperationException("recommendPrice not yet implemented");
    }


    @GetMapping("/api/v1/ai/jobs/{id}")
    @Operation(summary = "Poll an AI job's status")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PreAuthorize("isAuthenticated()")
    public org.springframework.http.ResponseEntity<com.artisanplatform.common.response.ApiResponse<AiJobResponse>> getJobStatus(@PathVariable UUID id) {
        // TODO: implement — wire to AiJobService; this stub returns 501 until business logic lands.
        throw new UnsupportedOperationException("getJobStatus not yet implemented");
    }
}
