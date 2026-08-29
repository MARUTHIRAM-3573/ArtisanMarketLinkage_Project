package com.artisanplatform.seller.service.impl;

import com.artisanplatform.common.exception.ResourceNotFoundException;
import com.artisanplatform.seller.domain.dto.request.SellerRequest;
import com.artisanplatform.seller.domain.dto.response.SellerResponse;
import com.artisanplatform.seller.domain.entity.Seller;
import com.artisanplatform.seller.domain.mapper.SellerMapper;
import com.artisanplatform.seller.repository.SellerRepository;
import com.artisanplatform.seller.service.SellerService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {

    private final SellerRepository sellerRepository;
    private final SellerMapper sellerMapper;

    @Override
    @Transactional
    public SellerResponse create(SellerRequest request) {
        // TODO: apply domain-specific validation and ownership checks here before persisting
        // (see docs/architecture/08_SECURITY_AND_VAULT.md Part C.3 for the per-module auth
        // enforcement pattern every write path in this platform must follow).
        Seller entity = sellerMapper.toEntity(request);
        Seller saved = sellerRepository.save(entity);
        return sellerMapper.toResponse(saved);
    }

    @Override
    public SellerResponse getById(UUID id) {
        Seller entity = sellerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", id));
        return sellerMapper.toResponse(entity);
    }
}
