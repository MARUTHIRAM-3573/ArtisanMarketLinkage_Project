package com.artisanplatform.payment.service.impl;

import com.artisanplatform.common.exception.ResourceNotFoundException;
import com.artisanplatform.payment.domain.dto.request.PaymentRequest;
import com.artisanplatform.payment.domain.dto.response.PaymentResponse;
import com.artisanplatform.payment.domain.entity.Payment;
import com.artisanplatform.payment.domain.mapper.PaymentMapper;
import com.artisanplatform.payment.repository.PaymentRepository;
import com.artisanplatform.payment.service.PaymentService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentResponse create(PaymentRequest request) {
        // TODO: apply domain-specific validation and ownership checks here before persisting
        // (see docs/architecture/08_SECURITY_AND_VAULT.md Part C.3 for the per-module auth
        // enforcement pattern every write path in this platform must follow).
        Payment entity = paymentMapper.toEntity(request);
        Payment saved = paymentRepository.save(entity);
        return paymentMapper.toResponse(saved);
    }

    @Override
    public PaymentResponse getById(UUID id) {
        Payment entity = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));
        return paymentMapper.toResponse(entity);
    }
}
