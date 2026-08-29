package com.artisanplatform.inventory.unit.service;

import com.artisanplatform.inventory.domain.dto.request.InventoryRequest;
import com.artisanplatform.inventory.domain.dto.response.InventoryResponse;
import com.artisanplatform.inventory.domain.entity.Inventory;
import com.artisanplatform.inventory.domain.mapper.InventoryMapper;
import com.artisanplatform.inventory.repository.InventoryRepository;
import com.artisanplatform.inventory.service.impl.InventoryServiceImpl;
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
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryMapper inventoryMapper;

    private InventoryServiceImpl inventoryService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        inventoryService = new InventoryServiceImpl(inventoryRepository, inventoryMapper);
    }

    @Test
    void getById_returnsMappedResponse_whenEntityExists() {
        // Arrange
        UUID id = UUID.randomUUID();
        Inventory entity = Inventory.builder().id(id).build();
        InventoryResponse expected = InventoryResponse.builder().id(id).build();
        when(inventoryRepository.findById(id)).thenReturn(Optional.of(entity));
        when(inventoryMapper.toResponse(entity)).thenReturn(expected);

        // Act
        InventoryResponse actual = inventoryService.getById(id);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getById_throws_whenEntityDoesNotExist() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(inventoryRepository.findById(id)).thenReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> inventoryService.getById(id))
                .isInstanceOf(com.artisanplatform.common.exception.ResourceNotFoundException.class);
    }

    @Test
    void create_persistsAndReturnsMappedResponse() {
        // Arrange
        InventoryRequest request = InventoryRequest.builder().build();
        Inventory entity = Inventory.builder().build();
        Inventory saved = Inventory.builder().id(UUID.randomUUID()).build();
        InventoryResponse expected = InventoryResponse.builder().id(saved.getId()).build();
        when(inventoryMapper.toEntity(request)).thenReturn(entity);
        when(inventoryRepository.save(entity)).thenReturn(saved);
        when(inventoryMapper.toResponse(saved)).thenReturn(expected);

        // Act
        InventoryResponse actual = inventoryService.create(request);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }
}
