package com.artisanplatform.auth.controller;

import com.artisanplatform.auth.domain.dto.request.UserRequest;
import com.artisanplatform.auth.domain.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Identity schema — authentication, users, roles, addresses.
 * One method per endpoint defined for this module in docs/architecture/05_API_CONTRACTS.md.
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final com.artisanplatform.auth.service.UserService userService;

    @PostMapping("/api/v1/auth/register")
    @Operation(summary = "Register a new user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    public org.springframework.http.ResponseEntity<com.artisanplatform.common.response.ApiResponse<UserResponse>> registerUser(@Valid @RequestBody UserRequest request) {
        // TODO: implement — wire to UserService; this stub returns 501 until business logic lands.
        throw new UnsupportedOperationException("registerUser not yet implemented");
    }


    @PostMapping("/api/v1/auth/login")
    @Operation(summary = "Authenticate and receive a bearer token")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    public org.springframework.http.ResponseEntity<com.artisanplatform.common.response.ApiResponse<UserResponse>> login() {
        // TODO: implement — wire to UserService; this stub returns 501 until business logic lands.
        throw new UnsupportedOperationException("login not yet implemented");
    }


    @GetMapping("/api/v1/auth/me")
    @Operation(summary = "Get the authenticated user's profile")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PreAuthorize("isAuthenticated()")
    public org.springframework.http.ResponseEntity<com.artisanplatform.common.response.ApiResponse<UserResponse>> getCurrentUser() {
        // TODO: implement — wire to UserService; this stub returns 501 until business logic lands.
        throw new UnsupportedOperationException("getCurrentUser not yet implemented");
    }
}
