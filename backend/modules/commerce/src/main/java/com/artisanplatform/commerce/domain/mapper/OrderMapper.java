package com.artisanplatform.commerce.domain.mapper;

import com.artisanplatform.commerce.domain.dto.request.OrderRequest;
import com.artisanplatform.commerce.domain.dto.response.OrderResponse;
import com.artisanplatform.commerce.domain.entity.Order;
import org.mapstruct.Mapper;

/** MapStruct mapper between Order entity and its request/response DTOs. */
@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponse toResponse(Order entity);

    Order toEntity(OrderRequest request);
}
