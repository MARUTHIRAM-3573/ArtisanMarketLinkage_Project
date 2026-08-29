package com.artisanplatform.commerce.unit.service;

import com.artisanplatform.commerce.domain.dto.request.OrderRequest;
import com.artisanplatform.commerce.domain.dto.response.OrderResponse;
import com.artisanplatform.commerce.domain.entity.Order;
import com.artisanplatform.commerce.domain.mapper.OrderMapper;
import com.artisanplatform.commerce.repository.OrderRepository;
import com.artisanplatform.commerce.service.impl.OrderServiceImpl;
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
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    private OrderServiceImpl orderService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository, orderMapper);
    }

    @Test
    void getById_returnsMappedResponse_whenEntityExists() {
        // Arrange
        UUID id = UUID.randomUUID();
        Order entity = Order.builder().id(id).build();
        OrderResponse expected = OrderResponse.builder().id(id).build();
        when(orderRepository.findById(id)).thenReturn(Optional.of(entity));
        when(orderMapper.toResponse(entity)).thenReturn(expected);

        // Act
        OrderResponse actual = orderService.getById(id);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getById_throws_whenEntityDoesNotExist() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> orderService.getById(id))
                .isInstanceOf(com.artisanplatform.common.exception.ResourceNotFoundException.class);
    }

    @Test
    void create_persistsAndReturnsMappedResponse() {
        // Arrange
        OrderRequest request = OrderRequest.builder().build();
        Order entity = Order.builder().build();
        Order saved = Order.builder().id(UUID.randomUUID()).build();
        OrderResponse expected = OrderResponse.builder().id(saved.getId()).build();
        when(orderMapper.toEntity(request)).thenReturn(entity);
        when(orderRepository.save(entity)).thenReturn(saved);
        when(orderMapper.toResponse(saved)).thenReturn(expected);

        // Act
        OrderResponse actual = orderService.create(request);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }
}
