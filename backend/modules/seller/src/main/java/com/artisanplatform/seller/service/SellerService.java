package com.artisanplatform.seller.service;

import com.artisanplatform.seller.domain.dto.request.SellerRequest;
import com.artisanplatform.seller.domain.dto.response.SellerResponse;
import java.util.UUID;

public interface SellerService {

    SellerResponse create(SellerRequest request);

    SellerResponse getById(UUID id);
}
