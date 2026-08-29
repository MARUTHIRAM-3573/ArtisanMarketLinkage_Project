package com.artisanplatform.media.domain.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request payload for creating/updating a MediaAsset. Field set matches docs/architecture/05_API_CONTRACTS.md. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaAssetRequest {

    @NotBlank
    private String mediaType;

    @NotBlank
    private String storageProvider;

    @NotBlank
    private String storagePath;

    @NotBlank
    private String originalFilename;

    @NotBlank
    private String mimeType;

    @NotNull
    private Long fileSizeBytes;

    @NotBlank
    private String checksum;

    @NotBlank
    private String status;
}
