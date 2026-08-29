package com.artisanplatform.ai.domain.dto.request;

import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request payload for creating/updating a AiJob. Field set matches docs/architecture/05_API_CONTRACTS.md. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiJobRequest {

    @NotBlank
    private String jobType;

    @NotBlank
    private String status;

    @NotNull
    private UUID requestedByUserId;

    private String modelName;

    private String modelVersion;

    private String errorMessage;
}
