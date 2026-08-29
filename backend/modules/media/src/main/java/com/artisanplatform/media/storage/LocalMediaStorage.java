package com.artisanplatform.media.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Local-filesystem implementation of {@link MediaStorageService} — the MVP
 * storage provider (source §5, §12). Never exposes the raw filesystem path
 * to clients (principle #32.4); callers only ever see the generated UUID
 * path stored in media_assets.storage_path.
 */
@Component
public class LocalMediaStorage implements MediaStorageService {

    private final Path basePath;

    public LocalMediaStorage(@Value("${app.media.local-path:/app/uploads}") String basePath) {
        this.basePath = Path.of(basePath);
    }

    @Override
    public String store(String originalFilename, String mimeType, InputStream content) {
        try {
            Files.createDirectories(basePath);
            String extension = extractExtension(originalFilename);
            String generatedName = UUID.randomUUID() + extension;
            Path target = basePath.resolve(generatedName);
            Files.copy(content, target, StandardCopyOption.REPLACE_EXISTING);
            return target.toString();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to store media file", e);
        }
    }

    @Override
    public InputStream retrieve(String storagePath) {
        try {
            return Files.newInputStream(Path.of(storagePath));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to retrieve media file: " + storagePath, e);
        }
    }

    @Override
    public void delete(String storagePath) {
        try {
            Files.deleteIfExists(Path.of(storagePath));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to delete media file: " + storagePath, e);
        }
    }

    private String extractExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot) : "";
    }
}
