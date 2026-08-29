package com.artisanplatform.catalog.domain.mapper;

import com.artisanplatform.catalog.domain.dto.request.ProductRequest;
import com.artisanplatform.catalog.domain.dto.response.ProductResponse;
import com.artisanplatform.catalog.domain.entity.Product;
import org.mapstruct.Mapper;

/** MapStruct mapper between Product entity and its request/response DTOs. */
@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponse toResponse(Product entity);

    Product toEntity(ProductRequest request);
}
