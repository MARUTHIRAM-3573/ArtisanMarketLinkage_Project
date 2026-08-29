package com.artisanplatform.ai.service;

import com.artisanplatform.ai.domain.dto.request.AiJobRequest;
import com.artisanplatform.ai.domain.dto.response.AiJobResponse;
import java.util.UUID;

public interface AiJobService {

    AiJobResponse create(AiJobRequest request);

    AiJobResponse getById(UUID id);
}
