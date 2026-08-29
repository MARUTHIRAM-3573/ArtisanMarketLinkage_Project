package com.artisanplatform.market.unit.service;

import com.artisanplatform.market.domain.dto.request.MarketListingRequest;
import com.artisanplatform.market.domain.dto.response.MarketListingResponse;
import com.artisanplatform.market.domain.entity.MarketListing;
import com.artisanplatform.market.domain.mapper.MarketListingMapper;
import com.artisanplatform.market.repository.MarketListingRepository;
import com.artisanplatform.market.service.impl.MarketListingServiceImpl;
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
class MarketListingServiceTest {

    @Mock
    private MarketListingRepository marketListingRepository;

    @Mock
    private MarketListingMapper marketListingMapper;

    private MarketListingServiceImpl marketListingService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        marketListingService = new MarketListingServiceImpl(marketListingRepository, marketListingMapper);
    }

    @Test
    void getById_returnsMappedResponse_whenEntityExists() {
        // Arrange
        UUID id = UUID.randomUUID();
        MarketListing entity = MarketListing.builder().id(id).build();
        MarketListingResponse expected = MarketListingResponse.builder().id(id).build();
        when(marketListingRepository.findById(id)).thenReturn(Optional.of(entity));
        when(marketListingMapper.toResponse(entity)).thenReturn(expected);

        // Act
        MarketListingResponse actual = marketListingService.getById(id);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getById_throws_whenEntityDoesNotExist() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(marketListingRepository.findById(id)).thenReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> marketListingService.getById(id))
                .isInstanceOf(com.artisanplatform.common.exception.ResourceNotFoundException.class);
    }

    @Test
    void create_persistsAndReturnsMappedResponse() {
        // Arrange
        MarketListingRequest request = MarketListingRequest.builder().build();
        MarketListing entity = MarketListing.builder().build();
        MarketListing saved = MarketListing.builder().id(UUID.randomUUID()).build();
        MarketListingResponse expected = MarketListingResponse.builder().id(saved.getId()).build();
        when(marketListingMapper.toEntity(request)).thenReturn(entity);
        when(marketListingRepository.save(entity)).thenReturn(saved);
        when(marketListingMapper.toResponse(saved)).thenReturn(expected);

        // Act
        MarketListingResponse actual = marketListingService.create(request);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }
}
