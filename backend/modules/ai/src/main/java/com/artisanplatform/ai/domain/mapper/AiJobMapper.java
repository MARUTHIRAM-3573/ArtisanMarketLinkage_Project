package com.artisanplatform.ai.domain.mapper;

import com.artisanplatform.ai.domain.dto.request.AiJobRequest;
import com.artisanplatform.ai.domain.dto.response.AiJobResponse;
import com.artisanplatform.ai.domain.entity.AiJob;
import org.mapstruct.Mapper;

/** MapStruct mapper between AiJob entity and its request/response DTOs. */
@Mapper(componentModel = "spring")
public interface AiJobMapper {

    AiJobResponse toResponse(AiJob entity);

    AiJob toEntity(AiJobRequest request);
}
