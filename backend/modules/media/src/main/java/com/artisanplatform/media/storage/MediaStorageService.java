package com.artisanplatform.media.storage;

import java.io.InputStream;

/**
 * Storage abstraction so the rest of the platform never depends on where
 * media binaries physically live (source §12: "Future migration to object
 * storage should be possible through an abstraction such as
 * MediaStorageService"). {@link LocalMediaStorage} is the only
 * implementation for MVP; an {@code S3MediaStorage} implementation is
 * future-compatible, not built now (docs/architecture/01_PRODUCT_SCOPE.md §8.2).
 */
public interface MediaStorageService {

    /** Stores the given content under a generated path and returns the storage path to persist on media_assets.storage_path. */
    String store(String originalFilename, String mimeType, InputStream content);

    InputStream retrieve(String storagePath);

    void delete(String storagePath);
}
