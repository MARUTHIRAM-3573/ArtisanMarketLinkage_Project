package com.artisanplatform.media.domain.mapper;

import com.artisanplatform.media.domain.dto.request.MediaAssetRequest;
import com.artisanplatform.media.domain.dto.response.MediaAssetResponse;
import com.artisanplatform.media.domain.entity.MediaAsset;
import org.mapstruct.Mapper;

/** MapStruct mapper between MediaAsset entity and its request/response DTOs. */
@Mapper(componentModel = "spring")
public interface MediaAssetMapper {

    MediaAssetResponse toResponse(MediaAsset entity);

    MediaAsset toEntity(MediaAssetRequest request);
}
