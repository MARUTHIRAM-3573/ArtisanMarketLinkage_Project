package com.artisanplatform.auth.service;

import com.artisanplatform.auth.domain.dto.request.UserRequest;
import com.artisanplatform.auth.domain.dto.response.UserResponse;
import java.util.UUID;

public interface UserService {

    UserResponse create(UserRequest request);

    UserResponse getById(UUID id);
}
