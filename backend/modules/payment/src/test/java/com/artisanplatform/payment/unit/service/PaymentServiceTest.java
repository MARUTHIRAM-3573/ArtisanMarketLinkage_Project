package com.artisanplatform.payment.unit.service;

import com.artisanplatform.payment.domain.dto.request.PaymentRequest;
import com.artisanplatform.payment.domain.dto.response.PaymentResponse;
import com.artisanplatform.payment.domain.entity.Payment;
import com.artisanplatform.payment.domain.mapper.PaymentMapper;
import com.artisanplatform.payment.repository.PaymentRepository;
import com.artisanplatform.payment.service.impl.PaymentServiceImpl;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    private PaymentServiceImpl paymentService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        paymentService = new PaymentServiceImpl(paymentRepository, paymentMapper);
    }

    @Test
    void getById_returnsMappedResponse_whenEntityExists() {
        // Arrange
        UUID id = UUID.randomUUID();
        Payment entity = Payment.builder().id(id).build();
        PaymentResponse expected = PaymentResponse.builder().id(id).build();
        when(paymentRepository.findById(id)).thenReturn(Optional.of(entity));
        when(paymentMapper.toResponse(entity)).thenReturn(expected);

        // Act
        PaymentResponse actual = paymentService.getById(id);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getById_throws_whenEntityDoesNotExist() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(paymentRepository.findById(id)).thenReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> paymentService.getById(id))
                .isInstanceOf(com.artisanplatform.common.exception.ResourceNotFoundException.class);
    }

    @Test
    void create_persistsAndReturnsMappedResponse() {
        // Arrange
        PaymentRequest request = PaymentRequest.builder().build();
        Payment entity = Payment.builder().build();
        Payment saved = Payment.builder().id(UUID.randomUUID()).build();
        PaymentResponse expected = PaymentResponse.builder().id(saved.getId()).build();
        when(paymentMapper.toEntity(request)).thenReturn(entity);
        when(paymentRepository.save(entity)).thenReturn(saved);
        when(paymentMapper.toResponse(saved)).thenReturn(expected);

        // Act
        PaymentResponse actual = paymentService.create(request);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }
}
