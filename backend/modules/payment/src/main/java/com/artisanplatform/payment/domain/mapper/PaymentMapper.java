package com.artisanplatform.payment.domain.mapper;

import com.artisanplatform.payment.domain.dto.request.PaymentRequest;
import com.artisanplatform.payment.domain.dto.response.PaymentResponse;
import com.artisanplatform.payment.domain.entity.Payment;
import org.mapstruct.Mapper;

/** MapStruct mapper between Payment entity and its request/response DTOs. */
@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentResponse toResponse(Payment entity);

    Payment toEntity(PaymentRequest request);
}
