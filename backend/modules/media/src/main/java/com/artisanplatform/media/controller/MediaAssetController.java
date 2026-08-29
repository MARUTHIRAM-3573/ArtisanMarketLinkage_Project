package com.artisanplatform.media.controller;

import com.artisanplatform.media.domain.dto.request.MediaAssetRequest;
import com.artisanplatform.media.domain.dto.response.MediaAssetResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Media schema — media asset metadata + MediaStorageService abstraction (source §11-12).
 * One method per endpoint defined for this module in docs/architecture/05_API_CONTRACTS.md.
 */
@RestController
@RequiredArgsConstructor
public class MediaAssetController {

    private final com.artisanplatform.media.service.MediaAssetService mediaAssetService;

    @PostMapping("/api/v1/media/upload")
    @Operation(summary = "Upload a media file")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PreAuthorize("isAuthenticated()")
    public org.springframework.http.ResponseEntity<com.artisanplatform.common.response.ApiResponse<MediaAssetResponse>> uploadMedia(@Valid @RequestBody MediaAssetRequest request) {
        // TODO: implement — wire to MediaAssetService; this stub returns 501 until business logic lands.
        throw new UnsupportedOperationException("uploadMedia not yet implemented");
    }


    @GetMapping("/api/v1/media/{id}")
    @Operation(summary = "Get media asset metadata")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PreAuthorize("isAuthenticated()")
    public org.springframework.http.ResponseEntity<com.artisanplatform.common.response.ApiResponse<MediaAssetResponse>> getMediaAsset(@PathVariable UUID id) {
        // TODO: implement — wire to MediaAssetService; this stub returns 501 until business logic lands.
        throw new UnsupportedOperationException("getMediaAsset not yet implemented");
    }
}
