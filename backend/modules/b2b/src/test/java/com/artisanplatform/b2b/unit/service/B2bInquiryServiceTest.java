package com.artisanplatform.b2b.unit.service;

import com.artisanplatform.b2b.domain.dto.request.B2bInquiryRequest;
import com.artisanplatform.b2b.domain.dto.response.B2bInquiryResponse;
import com.artisanplatform.b2b.domain.entity.B2bInquiry;
import com.artisanplatform.b2b.domain.mapper.B2bInquiryMapper;
import com.artisanplatform.b2b.repository.B2bInquiryRepository;
import com.artisanplatform.b2b.service.impl.B2bInquiryServiceImpl;
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
class B2bInquiryServiceTest {

    @Mock
    private B2bInquiryRepository b2bInquiryRepository;

    @Mock
    private B2bInquiryMapper b2bInquiryMapper;

    private B2bInquiryServiceImpl b2bInquiryService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        b2bInquiryService = new B2bInquiryServiceImpl(b2bInquiryRepository, b2bInquiryMapper);
    }

    @Test
    void getById_returnsMappedResponse_whenEntityExists() {
        // Arrange
        UUID id = UUID.randomUUID();
        B2bInquiry entity = B2bInquiry.builder().id(id).build();
        B2bInquiryResponse expected = B2bInquiryResponse.builder().id(id).build();
        when(b2bInquiryRepository.findById(id)).thenReturn(Optional.of(entity));
        when(b2bInquiryMapper.toResponse(entity)).thenReturn(expected);

        // Act
        B2bInquiryResponse actual = b2bInquiryService.getById(id);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getById_throws_whenEntityDoesNotExist() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(b2bInquiryRepository.findById(id)).thenReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> b2bInquiryService.getById(id))
                .isInstanceOf(com.artisanplatform.common.exception.ResourceNotFoundException.class);
    }

    @Test
    void create_persistsAndReturnsMappedResponse() {
        // Arrange
        B2bInquiryRequest request = B2bInquiryRequest.builder().build();
        B2bInquiry entity = B2bInquiry.builder().build();
        B2bInquiry saved = B2bInquiry.builder().id(UUID.randomUUID()).build();
        B2bInquiryResponse expected = B2bInquiryResponse.builder().id(saved.getId()).build();
        when(b2bInquiryMapper.toEntity(request)).thenReturn(entity);
        when(b2bInquiryRepository.save(entity)).thenReturn(saved);
        when(b2bInquiryMapper.toResponse(saved)).thenReturn(expected);

        // Act
        B2bInquiryResponse actual = b2bInquiryService.create(request);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }
}
