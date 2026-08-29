package com.artisanplatform.b2b.domain.mapper;

import com.artisanplatform.b2b.domain.dto.request.B2bInquiryRequest;
import com.artisanplatform.b2b.domain.dto.response.B2bInquiryResponse;
import com.artisanplatform.b2b.domain.entity.B2bInquiry;
import org.mapstruct.Mapper;

/** MapStruct mapper between B2bInquiry entity and its request/response DTOs. */
@Mapper(componentModel = "spring")
public interface B2bInquiryMapper {

    B2bInquiryResponse toResponse(B2bInquiry entity);

    B2bInquiry toEntity(B2bInquiryRequest request);
}
