package com.artisanplatform.seller.domain.mapper;

import com.artisanplatform.seller.domain.dto.request.SellerRequest;
import com.artisanplatform.seller.domain.dto.response.SellerResponse;
import com.artisanplatform.seller.domain.entity.Seller;
import org.mapstruct.Mapper;

/** MapStruct mapper between Seller entity and its request/response DTOs. */
@Mapper(componentModel = "spring")
public interface SellerMapper {

    SellerResponse toResponse(Seller entity);

    Seller toEntity(SellerRequest request);
}
