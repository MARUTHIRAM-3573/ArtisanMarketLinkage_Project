package com.artisanplatform.auth.domain.entity;

import com.artisanplatform.common.audit.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Maps to identity.users (database/migrations — see the schema's create migration).
 * See docs/architecture/04_DATA_MODEL_AND_OWNERSHIP.md for the full field
 * reference, constraints, and cross-service ownership rules.
 */
@Entity
@Table(name = "users", schema = "identity")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "phone_number", nullable = true)
    private String phoneNumber;

    @Column(name = "account_status", nullable = false)
    private String accountStatus;

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified;

    @Column(name = "preferred_language", nullable = true)
    private String preferredLanguage;
}
