package com.artisanplatform.seller.unit.service;

import com.artisanplatform.seller.domain.dto.request.SellerRequest;
import com.artisanplatform.seller.domain.dto.response.SellerResponse;
import com.artisanplatform.seller.domain.entity.Seller;
import com.artisanplatform.seller.domain.mapper.SellerMapper;
import com.artisanplatform.seller.repository.SellerRepository;
import com.artisanplatform.seller.service.impl.SellerServiceImpl;
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
class SellerServiceTest {

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private SellerMapper sellerMapper;

    private SellerServiceImpl sellerService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        sellerService = new SellerServiceImpl(sellerRepository, sellerMapper);
    }

    @Test
    void getById_returnsMappedResponse_whenEntityExists() {
        // Arrange
        UUID id = UUID.randomUUID();
        Seller entity = Seller.builder().id(id).build();
        SellerResponse expected = SellerResponse.builder().id(id).build();
        when(sellerRepository.findById(id)).thenReturn(Optional.of(entity));
        when(sellerMapper.toResponse(entity)).thenReturn(expected);

        // Act
        SellerResponse actual = sellerService.getById(id);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getById_throws_whenEntityDoesNotExist() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(sellerRepository.findById(id)).thenReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> sellerService.getById(id))
                .isInstanceOf(com.artisanplatform.common.exception.ResourceNotFoundException.class);
    }

    @Test
    void create_persistsAndReturnsMappedResponse() {
        // Arrange
        SellerRequest request = SellerRequest.builder().build();
        Seller entity = Seller.builder().build();
        Seller saved = Seller.builder().id(UUID.randomUUID()).build();
        SellerResponse expected = SellerResponse.builder().id(saved.getId()).build();
        when(sellerMapper.toEntity(request)).thenReturn(entity);
        when(sellerRepository.save(entity)).thenReturn(saved);
        when(sellerMapper.toResponse(saved)).thenReturn(expected);

        // Act
        SellerResponse actual = sellerService.create(request);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }
}
