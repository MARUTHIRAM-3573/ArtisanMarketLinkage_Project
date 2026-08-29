package com.artisanplatform.pricing.service;

import com.artisanplatform.pricing.domain.dto.request.SkuPriceRequest;
import com.artisanplatform.pricing.domain.dto.response.SkuPriceResponse;
import java.util.UUID;

public interface SkuPriceService {

    SkuPriceResponse create(SkuPriceRequest request);

    SkuPriceResponse getById(UUID id);
}
