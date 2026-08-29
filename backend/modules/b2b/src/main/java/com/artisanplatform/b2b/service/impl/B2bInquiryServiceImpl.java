package com.artisanplatform.b2b.service.impl;

import com.artisanplatform.common.exception.ResourceNotFoundException;
import com.artisanplatform.b2b.domain.dto.request.B2bInquiryRequest;
import com.artisanplatform.b2b.domain.dto.response.B2bInquiryResponse;
import com.artisanplatform.b2b.domain.entity.B2bInquiry;
import com.artisanplatform.b2b.domain.mapper.B2bInquiryMapper;
import com.artisanplatform.b2b.repository.B2bInquiryRepository;
import com.artisanplatform.b2b.service.B2bInquiryService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class B2bInquiryServiceImpl implements B2bInquiryService {

    private final B2bInquiryRepository b2bInquiryRepository;
    private final B2bInquiryMapper b2bInquiryMapper;

    @Override
    @Transactional
    public B2bInquiryResponse create(B2bInquiryRequest request) {
        // TODO: apply domain-specific validation and ownership checks here before persisting
        // (see docs/architecture/08_SECURITY_AND_VAULT.md Part C.3 for the per-module auth
        // enforcement pattern every write path in this platform must follow).
        B2bInquiry entity = b2bInquiryMapper.toEntity(request);
        B2bInquiry saved = b2bInquiryRepository.save(entity);
        return b2bInquiryMapper.toResponse(saved);
    }

    @Override
    public B2bInquiryResponse getById(UUID id) {
        B2bInquiry entity = b2bInquiryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2bInquiry", id));
        return b2bInquiryMapper.toResponse(entity);
    }
}
