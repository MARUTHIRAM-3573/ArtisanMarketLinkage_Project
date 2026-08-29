package com.artisanplatform.catalog.service.impl;

import com.artisanplatform.common.exception.ResourceNotFoundException;
import com.artisanplatform.catalog.domain.dto.request.ProductRequest;
import com.artisanplatform.catalog.domain.dto.response.ProductResponse;
import com.artisanplatform.catalog.domain.entity.Product;
import com.artisanplatform.catalog.domain.mapper.ProductMapper;
import com.artisanplatform.catalog.repository.ProductRepository;
import com.artisanplatform.catalog.service.ProductService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {
        // TODO: apply domain-specific validation and ownership checks here before persisting
        // (see docs/architecture/08_SECURITY_AND_VAULT.md Part C.3 for the per-module auth
        // enforcement pattern every write path in this platform must follow).
        Product entity = productMapper.toEntity(request);
        Product saved = productRepository.save(entity);
        return productMapper.toResponse(saved);
    }

    @Override
    public ProductResponse getById(UUID id) {
        Product entity = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        return productMapper.toResponse(entity);
    }
}
