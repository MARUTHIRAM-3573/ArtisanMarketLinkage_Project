package com.artisanplatform.ai.unit.service;

import com.artisanplatform.ai.domain.dto.request.AiJobRequest;
import com.artisanplatform.ai.domain.dto.response.AiJobResponse;
import com.artisanplatform.ai.domain.entity.AiJob;
import com.artisanplatform.ai.domain.mapper.AiJobMapper;
import com.artisanplatform.ai.repository.AiJobRepository;
import com.artisanplatform.ai.service.impl.AiJobServiceImpl;
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
class AiJobServiceTest {

    @Mock
    private AiJobRepository aiJobRepository;

    @Mock
    private AiJobMapper aiJobMapper;

    private AiJobServiceImpl aiJobService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        aiJobService = new AiJobServiceImpl(aiJobRepository, aiJobMapper);
    }

    @Test
    void getById_returnsMappedResponse_whenEntityExists() {
        // Arrange
        UUID id = UUID.randomUUID();
        AiJob entity = AiJob.builder().id(id).build();
        AiJobResponse expected = AiJobResponse.builder().id(id).build();
        when(aiJobRepository.findById(id)).thenReturn(Optional.of(entity));
        when(aiJobMapper.toResponse(entity)).thenReturn(expected);

        // Act
        AiJobResponse actual = aiJobService.getById(id);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getById_throws_whenEntityDoesNotExist() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(aiJobRepository.findById(id)).thenReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> aiJobService.getById(id))
                .isInstanceOf(com.artisanplatform.common.exception.ResourceNotFoundException.class);
    }

    @Test
    void create_persistsAndReturnsMappedResponse() {
        // Arrange
        AiJobRequest request = AiJobRequest.builder().build();
        AiJob entity = AiJob.builder().build();
        AiJob saved = AiJob.builder().id(UUID.randomUUID()).build();
        AiJobResponse expected = AiJobResponse.builder().id(saved.getId()).build();
        when(aiJobMapper.toEntity(request)).thenReturn(entity);
        when(aiJobRepository.save(entity)).thenReturn(saved);
        when(aiJobMapper.toResponse(saved)).thenReturn(expected);

        // Act
        AiJobResponse actual = aiJobService.create(request);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }
}
