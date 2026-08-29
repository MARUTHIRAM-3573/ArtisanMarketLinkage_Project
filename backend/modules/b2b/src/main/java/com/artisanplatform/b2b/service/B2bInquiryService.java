package com.artisanplatform.b2b.service;

import com.artisanplatform.b2b.domain.dto.request.B2bInquiryRequest;
import com.artisanplatform.b2b.domain.dto.response.B2bInquiryResponse;
import java.util.UUID;

public interface B2bInquiryService {

    B2bInquiryResponse create(B2bInquiryRequest request);

    B2bInquiryResponse getById(UUID id);
}
