package com.artisanplatform.media.domain.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Response payload for a MediaAsset. Field set matches docs/architecture/05_API_CONTRACTS.md. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaAssetResponse {

    private UUID id;

    private String mediaType;

    private String storageProvider;

    private String storagePath;

    private String originalFilename;

    private String mimeType;

    private Long fileSizeBytes;

    private String checksum;

    private String status;
}
