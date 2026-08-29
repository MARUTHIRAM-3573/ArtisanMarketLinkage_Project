package com.artisanplatform.commerce.service.impl;

import com.artisanplatform.common.exception.ResourceNotFoundException;
import com.artisanplatform.commerce.domain.dto.request.OrderRequest;
import com.artisanplatform.commerce.domain.dto.response.OrderResponse;
import com.artisanplatform.commerce.domain.entity.Order;
import com.artisanplatform.commerce.domain.mapper.OrderMapper;
import com.artisanplatform.commerce.repository.OrderRepository;
import com.artisanplatform.commerce.service.OrderService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponse create(OrderRequest request) {
        // TODO: apply domain-specific validation and ownership checks here before persisting
        // (see docs/architecture/08_SECURITY_AND_VAULT.md Part C.3 for the per-module auth
        // enforcement pattern every write path in this platform must follow).
        Order entity = orderMapper.toEntity(request);
        Order saved = orderRepository.save(entity);
        return orderMapper.toResponse(saved);
    }

    @Override
    public OrderResponse getById(UUID id) {
        Order entity = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        return orderMapper.toResponse(entity);
    }
}
