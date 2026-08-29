package com.artisanplatform.auth.service.impl;

import com.artisanplatform.common.exception.ResourceNotFoundException;
import com.artisanplatform.auth.domain.dto.request.UserRequest;
import com.artisanplatform.auth.domain.dto.response.UserResponse;
import com.artisanplatform.auth.domain.entity.User;
import com.artisanplatform.auth.domain.mapper.UserMapper;
import com.artisanplatform.auth.repository.UserRepository;
import com.artisanplatform.auth.service.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse create(UserRequest request) {
        // TODO: apply domain-specific validation and ownership checks here before persisting
        // (see docs/architecture/08_SECURITY_AND_VAULT.md Part C.3 for the per-module auth
        // enforcement pattern every write path in this platform must follow).
        User entity = userMapper.toEntity(request);
        User saved = userRepository.save(entity);
        return userMapper.toResponse(saved);
    }

    @Override
    public UserResponse getById(UUID id) {
        User entity = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return userMapper.toResponse(entity);
    }
}
