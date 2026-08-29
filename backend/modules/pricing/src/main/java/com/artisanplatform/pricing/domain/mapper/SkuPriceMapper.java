package com.artisanplatform.pricing.domain.mapper;

import com.artisanplatform.pricing.domain.dto.request.SkuPriceRequest;
import com.artisanplatform.pricing.domain.dto.response.SkuPriceResponse;
import com.artisanplatform.pricing.domain.entity.SkuPrice;
import org.mapstruct.Mapper;

/** MapStruct mapper between SkuPrice entity and its request/response DTOs. */
@Mapper(componentModel = "spring")
public interface SkuPriceMapper {

    SkuPriceResponse toResponse(SkuPrice entity);

    SkuPrice toEntity(SkuPriceRequest request);
}
