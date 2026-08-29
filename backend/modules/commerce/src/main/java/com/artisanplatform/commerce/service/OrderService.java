package com.artisanplatform.commerce.service;

import com.artisanplatform.commerce.domain.dto.request.OrderRequest;
import com.artisanplatform.commerce.domain.dto.response.OrderResponse;
import java.util.UUID;

public interface OrderService {

    OrderResponse create(OrderRequest request);

    OrderResponse getById(UUID id);
}
