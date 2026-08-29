package com.artisanplatform.inventory.service.impl;

import com.artisanplatform.common.exception.ResourceNotFoundException;
import com.artisanplatform.inventory.domain.dto.request.InventoryRequest;
import com.artisanplatform.inventory.domain.dto.response.InventoryResponse;
import com.artisanplatform.inventory.domain.entity.Inventory;
import com.artisanplatform.inventory.domain.mapper.InventoryMapper;
import com.artisanplatform.inventory.repository.InventoryRepository;
import com.artisanplatform.inventory.service.InventoryService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    @Override
    @Transactional
    public InventoryResponse create(InventoryRequest request) {
        // TODO: apply domain-specific validation and ownership checks here before persisting
        // (see docs/architecture/08_SECURITY_AND_VAULT.md Part C.3 for the per-module auth
        // enforcement pattern every write path in this platform must follow).
        Inventory entity = inventoryMapper.toEntity(request);
        Inventory saved = inventoryRepository.save(entity);
        return inventoryMapper.toResponse(saved);
    }

    @Override
    public InventoryResponse getById(UUID id) {
        Inventory entity = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", id));
        return inventoryMapper.toResponse(entity);
    }
}
