package com.artisanplatform.auth.unit.service;

import com.artisanplatform.auth.domain.dto.request.UserRequest;
import com.artisanplatform.auth.domain.dto.response.UserResponse;
import com.artisanplatform.auth.domain.entity.User;
import com.artisanplatform.auth.domain.mapper.UserMapper;
import com.artisanplatform.auth.repository.UserRepository;
import com.artisanplatform.auth.service.impl.UserServiceImpl;
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
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    private UserServiceImpl userService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, userMapper);
    }

    @Test
    void getById_returnsMappedResponse_whenEntityExists() {
        // Arrange
        UUID id = UUID.randomUUID();
        User entity = User.builder().id(id).build();
        UserResponse expected = UserResponse.builder().id(id).build();
        when(userRepository.findById(id)).thenReturn(Optional.of(entity));
        when(userMapper.toResponse(entity)).thenReturn(expected);

        // Act
        UserResponse actual = userService.getById(id);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getById_throws_whenEntityDoesNotExist() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> userService.getById(id))
                .isInstanceOf(com.artisanplatform.common.exception.ResourceNotFoundException.class);
    }

    @Test
    void create_persistsAndReturnsMappedResponse() {
        // Arrange
        UserRequest request = UserRequest.builder().build();
        User entity = User.builder().build();
        User saved = User.builder().id(UUID.randomUUID()).build();
        UserResponse expected = UserResponse.builder().id(saved.getId()).build();
        when(userMapper.toEntity(request)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(saved);
        when(userMapper.toResponse(saved)).thenReturn(expected);

        // Act
        UserResponse actual = userService.create(request);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }
}
