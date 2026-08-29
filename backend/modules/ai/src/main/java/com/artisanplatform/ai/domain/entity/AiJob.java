package com.artisanplatform.ai.domain.entity;

import com.artisanplatform.common.audit.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Maps to ai.ai_jobs (database/migrations — see the schema's create migration).
 * See docs/architecture/04_DATA_MODEL_AND_OWNERSHIP.md for the full field
 * reference, constraints, and cross-service ownership rules.
 */
@Entity
@Table(name = "ai_jobs", schema = "ai")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiJob extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "job_type", nullable = false)
    private String jobType;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "requested_by_user_id", nullable = false)
    private UUID requestedByUserId;

    @Column(name = "model_name", nullable = true)
    private String modelName;

    @Column(name = "model_version", nullable = true)
    private String modelVersion;

    @Column(name = "error_message", nullable = true)
    private String errorMessage;
}
