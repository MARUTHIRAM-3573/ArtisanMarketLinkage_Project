package com.artisanplatform.media.unit.service;

import com.artisanplatform.media.domain.dto.request.MediaAssetRequest;
import com.artisanplatform.media.domain.dto.response.MediaAssetResponse;
import com.artisanplatform.media.domain.entity.MediaAsset;
import com.artisanplatform.media.domain.mapper.MediaAssetMapper;
import com.artisanplatform.media.repository.MediaAssetRepository;
import com.artisanplatform.media.service.impl.MediaAssetServiceImpl;
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
class MediaAssetServiceTest {

    @Mock
    private MediaAssetRepository mediaAssetRepository;

    @Mock
    private MediaAssetMapper mediaAssetMapper;

    private MediaAssetServiceImpl mediaAssetService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        mediaAssetService = new MediaAssetServiceImpl(mediaAssetRepository, mediaAssetMapper);
    }

    @Test
    void getById_returnsMappedResponse_whenEntityExists() {
        // Arrange
        UUID id = UUID.randomUUID();
        MediaAsset entity = MediaAsset.builder().id(id).build();
        MediaAssetResponse expected = MediaAssetResponse.builder().id(id).build();
        when(mediaAssetRepository.findById(id)).thenReturn(Optional.of(entity));
        when(mediaAssetMapper.toResponse(entity)).thenReturn(expected);

        // Act
        MediaAssetResponse actual = mediaAssetService.getById(id);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getById_throws_whenEntityDoesNotExist() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(mediaAssetRepository.findById(id)).thenReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> mediaAssetService.getById(id))
                .isInstanceOf(com.artisanplatform.common.exception.ResourceNotFoundException.class);
    }

    @Test
    void create_persistsAndReturnsMappedResponse() {
        // Arrange
        MediaAssetRequest request = MediaAssetRequest.builder().build();
        MediaAsset entity = MediaAsset.builder().build();
        MediaAsset saved = MediaAsset.builder().id(UUID.randomUUID()).build();
        MediaAssetResponse expected = MediaAssetResponse.builder().id(saved.getId()).build();
        when(mediaAssetMapper.toEntity(request)).thenReturn(entity);
        when(mediaAssetRepository.save(entity)).thenReturn(saved);
        when(mediaAssetMapper.toResponse(saved)).thenReturn(expected);

        // Act
        MediaAssetResponse actual = mediaAssetService.create(request);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }
}
