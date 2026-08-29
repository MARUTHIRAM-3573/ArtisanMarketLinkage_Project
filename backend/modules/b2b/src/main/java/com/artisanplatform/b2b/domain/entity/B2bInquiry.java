package com.artisanplatform.b2b.domain.entity;

import com.artisanplatform.common.audit.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Maps to b2b.b2b_inquiries (database/migrations — see the schema's create migration).
 * See docs/architecture/04_DATA_MODEL_AND_OWNERSHIP.md for the full field
 * reference, constraints, and cross-service ownership rules.
 */
@Entity
@Table(name = "b2b_inquiries", schema = "b2b")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class B2bInquiry extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "b2b_buyer_id", nullable = false)
    private UUID b2bBuyerId;

    @Column(name = "seller_id", nullable = false)
    private UUID sellerId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "requested_quantity", nullable = false)
    private Integer requestedQuantity;

    @Column(name = "target_price", nullable = true)
    private BigDecimal targetPrice;

    @Column(name = "message", nullable = true)
    private String message;

    @Column(name = "delivery_requirement", nullable = true)
    private String deliveryRequirement;

    @Column(name = "status", nullable = false)
    private String status;
}
