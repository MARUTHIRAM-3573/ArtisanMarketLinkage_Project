package com.artisanplatform.pricing.unit.service;

import com.artisanplatform.pricing.domain.dto.request.SkuPriceRequest;
import com.artisanplatform.pricing.domain.dto.response.SkuPriceResponse;
import com.artisanplatform.pricing.domain.entity.SkuPrice;
import com.artisanplatform.pricing.domain.mapper.SkuPriceMapper;
import com.artisanplatform.pricing.repository.SkuPriceRepository;
import com.artisanplatform.pricing.service.impl.SkuPriceServiceImpl;
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
class SkuPriceServiceTest {

    @Mock
    private SkuPriceRepository skuPriceRepository;

    @Mock
    private SkuPriceMapper skuPriceMapper;

    private SkuPriceServiceImpl skuPriceService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        skuPriceService = new SkuPriceServiceImpl(skuPriceRepository, skuPriceMapper);
    }

    @Test
    void getById_returnsMappedResponse_whenEntityExists() {
        // Arrange
        UUID id = UUID.randomUUID();
        SkuPrice entity = SkuPrice.builder().id(id).build();
        SkuPriceResponse expected = SkuPriceResponse.builder().id(id).build();
        when(skuPriceRepository.findById(id)).thenReturn(Optional.of(entity));
        when(skuPriceMapper.toResponse(entity)).thenReturn(expected);

        // Act
        SkuPriceResponse actual = skuPriceService.getById(id);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getById_throws_whenEntityDoesNotExist() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(skuPriceRepository.findById(id)).thenReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> skuPriceService.getById(id))
                .isInstanceOf(com.artisanplatform.common.exception.ResourceNotFoundException.class);
    }

    @Test
    void create_persistsAndReturnsMappedResponse() {
        // Arrange
        SkuPriceRequest request = SkuPriceRequest.builder().build();
        SkuPrice entity = SkuPrice.builder().build();
        SkuPrice saved = SkuPrice.builder().id(UUID.randomUUID()).build();
        SkuPriceResponse expected = SkuPriceResponse.builder().id(saved.getId()).build();
        when(skuPriceMapper.toEntity(request)).thenReturn(entity);
        when(skuPriceRepository.save(entity)).thenReturn(saved);
        when(skuPriceMapper.toResponse(saved)).thenReturn(expected);

        // Act
        SkuPriceResponse actual = skuPriceService.create(request);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }
}
