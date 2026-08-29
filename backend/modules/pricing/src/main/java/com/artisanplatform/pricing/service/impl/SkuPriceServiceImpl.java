package com.artisanplatform.pricing.service.impl;

import com.artisanplatform.common.exception.ResourceNotFoundException;
import com.artisanplatform.pricing.domain.dto.request.SkuPriceRequest;
import com.artisanplatform.pricing.domain.dto.response.SkuPriceResponse;
import com.artisanplatform.pricing.domain.entity.SkuPrice;
import com.artisanplatform.pricing.domain.mapper.SkuPriceMapper;
import com.artisanplatform.pricing.repository.SkuPriceRepository;
import com.artisanplatform.pricing.service.SkuPriceService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SkuPriceServiceImpl implements SkuPriceService {

    private final SkuPriceRepository skuPriceRepository;
    private final SkuPriceMapper skuPriceMapper;

    @Override
    @Transactional
    public SkuPriceResponse create(SkuPriceRequest request) {
        // TODO: apply domain-specific validation and ownership checks here before persisting
        // (see docs/architecture/08_SECURITY_AND_VAULT.md Part C.3 for the per-module auth
        // enforcement pattern every write path in this platform must follow).
        SkuPrice entity = skuPriceMapper.toEntity(request);
        SkuPrice saved = skuPriceRepository.save(entity);
        return skuPriceMapper.toResponse(saved);
    }

    @Override
    public SkuPriceResponse getById(UUID id) {
        SkuPrice entity = skuPriceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SkuPrice", id));
        return skuPriceMapper.toResponse(entity);
    }
}
