package com.artisanplatform.catalog.service;

import com.artisanplatform.catalog.domain.dto.request.ProductRequest;
import com.artisanplatform.catalog.domain.dto.response.ProductResponse;
import java.util.UUID;

public interface ProductService {

    ProductResponse create(ProductRequest request);

    ProductResponse getById(UUID id);
}
