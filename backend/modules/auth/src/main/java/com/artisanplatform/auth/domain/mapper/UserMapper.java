package com.artisanplatform.auth.domain.mapper;

import com.artisanplatform.auth.domain.dto.request.UserRequest;
import com.artisanplatform.auth.domain.dto.response.UserResponse;
import com.artisanplatform.auth.domain.entity.User;
import org.mapstruct.Mapper;

/** MapStruct mapper between User entity and its request/response DTOs. */
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User entity);

    User toEntity(UserRequest request);
}
