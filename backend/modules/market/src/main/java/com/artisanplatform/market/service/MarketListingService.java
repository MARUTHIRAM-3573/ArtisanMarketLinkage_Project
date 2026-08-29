package com.artisanplatform.market.service;

import com.artisanplatform.market.domain.dto.request.MarketListingRequest;
import com.artisanplatform.market.domain.dto.response.MarketListingResponse;
import java.util.UUID;

public interface MarketListingService {

    MarketListingResponse create(MarketListingRequest request);

    MarketListingResponse getById(UUID id);
}
