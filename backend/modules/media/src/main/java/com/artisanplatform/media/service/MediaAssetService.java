package com.artisanplatform.media.service;

import com.artisanplatform.media.domain.dto.request.MediaAssetRequest;
import com.artisanplatform.media.domain.dto.response.MediaAssetResponse;
import java.util.UUID;

public interface MediaAssetService {

    MediaAssetResponse create(MediaAssetRequest request);

    MediaAssetResponse getById(UUID id);
}
