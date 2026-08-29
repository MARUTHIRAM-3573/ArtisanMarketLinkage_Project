package com.artisanplatform.payment.service;

import com.artisanplatform.payment.domain.dto.request.PaymentRequest;
import com.artisanplatform.payment.domain.dto.response.PaymentResponse;
import java.util.UUID;

public interface PaymentService {

    PaymentResponse create(PaymentRequest request);

    PaymentResponse getById(UUID id);
}
