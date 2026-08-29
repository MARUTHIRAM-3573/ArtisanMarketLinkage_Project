package com.artisanplatform.auth.domain.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Response payload for a User. Field set matches docs/architecture/05_API_CONTRACTS.md. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private UUID id;

    private String email;

    private String passwordHash;

    private String fullName;

    private String phoneNumber;

    private String accountStatus;

    private Boolean emailVerified;

    private String preferredLanguage;
}
