package com.artisanplatform.market.service.impl;

import com.artisanplatform.common.exception.ResourceNotFoundException;
import com.artisanplatform.market.domain.dto.request.MarketListingRequest;
import com.artisanplatform.market.domain.dto.response.MarketListingResponse;
import com.artisanplatform.market.domain.entity.MarketListing;
import com.artisanplatform.market.domain.mapper.MarketListingMapper;
import com.artisanplatform.market.repository.MarketListingRepository;
import com.artisanplatform.market.service.MarketListingService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarketListingServiceImpl implements MarketListingService {

    private final MarketListingRepository marketListingRepository;
    private final MarketListingMapper marketListingMapper;

    @Override
    @Transactional
    public MarketListingResponse create(MarketListingRequest request) {
        // TODO: apply domain-specific validation and ownership checks here before persisting
        // (see docs/architecture/08_SECURITY_AND_VAULT.md Part C.3 for the per-module auth
        // enforcement pattern every write path in this platform must follow).
        MarketListing entity = marketListingMapper.toEntity(request);
        MarketListing saved = marketListingRepository.save(entity);
        return marketListingMapper.toResponse(saved);
    }

    @Override
    public MarketListingResponse getById(UUID id) {
        MarketListing entity = marketListingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MarketListing", id));
        return marketListingMapper.toResponse(entity);
    }
}
