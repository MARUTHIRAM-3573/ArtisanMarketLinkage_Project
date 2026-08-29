package com.artisanplatform.inventory.service;

import com.artisanplatform.inventory.domain.dto.request.InventoryRequest;
import com.artisanplatform.inventory.domain.dto.response.InventoryResponse;
import java.util.UUID;

public interface InventoryService {

    InventoryResponse create(InventoryRequest request);

    InventoryResponse getById(UUID id);
}
