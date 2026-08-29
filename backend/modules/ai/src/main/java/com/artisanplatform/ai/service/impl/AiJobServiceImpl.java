package com.artisanplatform.ai.service.impl;

import com.artisanplatform.common.exception.ResourceNotFoundException;
import com.artisanplatform.ai.domain.dto.request.AiJobRequest;
import com.artisanplatform.ai.domain.dto.response.AiJobResponse;
import com.artisanplatform.ai.domain.entity.AiJob;
import com.artisanplatform.ai.domain.mapper.AiJobMapper;
import com.artisanplatform.ai.repository.AiJobRepository;
import com.artisanplatform.ai.service.AiJobService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AiJobServiceImpl implements AiJobService {

    private final AiJobRepository aiJobRepository;
    private final AiJobMapper aiJobMapper;

    @Override
    @Transactional
    public AiJobResponse create(AiJobRequest request) {
        // TODO: apply domain-specific validation and ownership checks here before persisting
        // (see docs/architecture/08_SECURITY_AND_VAULT.md Part C.3 for the per-module auth
        // enforcement pattern every write path in this platform must follow).
        AiJob entity = aiJobMapper.toEntity(request);
        AiJob saved = aiJobRepository.save(entity);
        return aiJobMapper.toResponse(saved);
    }

    @Override
    public AiJobResponse getById(UUID id) {
        AiJob entity = aiJobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AiJob", id));
        return aiJobMapper.toResponse(entity);
    }
}
