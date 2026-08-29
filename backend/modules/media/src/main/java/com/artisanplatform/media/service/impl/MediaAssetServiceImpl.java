package com.artisanplatform.media.service.impl;

import com.artisanplatform.common.exception.ResourceNotFoundException;
import com.artisanplatform.media.domain.dto.request.MediaAssetRequest;
import com.artisanplatform.media.domain.dto.response.MediaAssetResponse;
import com.artisanplatform.media.domain.entity.MediaAsset;
import com.artisanplatform.media.domain.mapper.MediaAssetMapper;
import com.artisanplatform.media.repository.MediaAssetRepository;
import com.artisanplatform.media.service.MediaAssetService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MediaAssetServiceImpl implements MediaAssetService {

    private final MediaAssetRepository mediaAssetRepository;
    private final MediaAssetMapper mediaAssetMapper;

    @Override
    @Transactional
    public MediaAssetResponse create(MediaAssetRequest request) {
        // TODO: apply domain-specific validation and ownership checks here before persisting
        // (see docs/architecture/08_SECURITY_AND_VAULT.md Part C.3 for the per-module auth
        // enforcement pattern every write path in this platform must follow).
        MediaAsset entity = mediaAssetMapper.toEntity(request);
        MediaAsset saved = mediaAssetRepository.save(entity);
        return mediaAssetMapper.toResponse(saved);
    }

    @Override
    public MediaAssetResponse getById(UUID id) {
        MediaAsset entity = mediaAssetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MediaAsset", id));
        return mediaAssetMapper.toResponse(entity);
    }
}
