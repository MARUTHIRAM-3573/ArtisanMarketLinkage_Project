package com.artisanplatform.ai.domain.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Response payload for a AiJob. Field set matches docs/architecture/05_API_CONTRACTS.md. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiJobResponse {

    private UUID id;

    private String jobType;

    private String status;

    private UUID requestedByUserId;

    private String modelName;

    private String modelVersion;

    private String errorMessage;
}
