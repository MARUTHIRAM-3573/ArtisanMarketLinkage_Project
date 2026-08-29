package com.artisanplatform.seller.domain.entity;

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
 * Maps to seller.sellers (database/migrations — see the schema's create migration).
 * See docs/architecture/04_DATA_MODEL_AND_OWNERSHIP.md for the full field
 * reference, constraints, and cross-service ownership rules.
 */
@Entity
@Table(name = "sellers", schema = "seller")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seller extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "seller_type", nullable = false)
    private String sellerType;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "verification_status", nullable = false)
    private String verificationStatus;
}
