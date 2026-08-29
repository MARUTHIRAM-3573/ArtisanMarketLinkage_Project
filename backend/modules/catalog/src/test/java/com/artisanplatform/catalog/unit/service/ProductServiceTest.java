package com.artisanplatform.catalog.unit.service;

import com.artisanplatform.catalog.domain.dto.request.ProductRequest;
import com.artisanplatform.catalog.domain.dto.response.ProductResponse;
import com.artisanplatform.catalog.domain.entity.Product;
import com.artisanplatform.catalog.domain.mapper.ProductMapper;
import com.artisanplatform.catalog.repository.ProductRepository;
import com.artisanplatform.catalog.service.impl.ProductServiceImpl;
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
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    private ProductServiceImpl productService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        productService = new ProductServiceImpl(productRepository, productMapper);
    }

    @Test
    void getById_returnsMappedResponse_whenEntityExists() {
        // Arrange
        UUID id = UUID.randomUUID();
        Product entity = Product.builder().id(id).build();
        ProductResponse expected = ProductResponse.builder().id(id).build();
        when(productRepository.findById(id)).thenReturn(Optional.of(entity));
        when(productMapper.toResponse(entity)).thenReturn(expected);

        // Act
        ProductResponse actual = productService.getById(id);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getById_throws_whenEntityDoesNotExist() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> productService.getById(id))
                .isInstanceOf(com.artisanplatform.common.exception.ResourceNotFoundException.class);
    }

    @Test
    void create_persistsAndReturnsMappedResponse() {
        // Arrange
        ProductRequest request = ProductRequest.builder().build();
        Product entity = Product.builder().build();
        Product saved = Product.builder().id(UUID.randomUUID()).build();
        ProductResponse expected = ProductResponse.builder().id(saved.getId()).build();
        when(productMapper.toEntity(request)).thenReturn(entity);
        when(productRepository.save(entity)).thenReturn(saved);
        when(productMapper.toResponse(saved)).thenReturn(expected);

        // Act
        ProductResponse actual = productService.create(request);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }
}
