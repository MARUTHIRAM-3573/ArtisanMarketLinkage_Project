package com.artisanplatform.market.domain.mapper;

import com.artisanplatform.market.domain.dto.request.MarketListingRequest;
import com.artisanplatform.market.domain.dto.response.MarketListingResponse;
import com.artisanplatform.market.domain.entity.MarketListing;
import org.mapstruct.Mapper;

/** MapStruct mapper between MarketListing entity and its request/response DTOs. */
@Mapper(componentModel = "spring")
public interface MarketListingMapper {

    MarketListingResponse toResponse(MarketListing entity);

    MarketListing toEntity(MarketListingRequest request);
}
