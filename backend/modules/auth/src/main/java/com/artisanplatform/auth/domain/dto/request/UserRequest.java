package com.artisanplatform.auth.domain.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request payload for creating/updating a User. Field set matches docs/architecture/05_API_CONTRACTS.md. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String passwordHash;

    @NotBlank
    private String fullName;

    private String phoneNumber;

    @NotBlank
    private String accountStatus;

    @NotNull
    private Boolean emailVerified;

    private String preferredLanguage;
}
