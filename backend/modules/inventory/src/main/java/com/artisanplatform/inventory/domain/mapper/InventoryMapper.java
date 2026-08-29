package com.artisanplatform.inventory.domain.mapper;

import com.artisanplatform.inventory.domain.dto.request.InventoryRequest;
import com.artisanplatform.inventory.domain.dto.response.InventoryResponse;
import com.artisanplatform.inventory.domain.entity.Inventory;
import org.mapstruct.Mapper;

/** MapStruct mapper between Inventory entity and its request/response DTOs. */
@Mapper(componentModel = "spring")
public interface InventoryMapper {

    InventoryResponse toResponse(Inventory entity);

    Inventory toEntity(InventoryRequest request);
}
