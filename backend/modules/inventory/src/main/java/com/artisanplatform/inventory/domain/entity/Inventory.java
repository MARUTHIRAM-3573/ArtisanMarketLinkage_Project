package com.artisanplatform.inventory.domain.entity;

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
 * Maps to inventory.inventories (database/migrations — see the schema's create migration).
 * See docs/architecture/04_DATA_MODEL_AND_OWNERSHIP.md for the full field
 * reference, constraints, and cross-service ownership rules.
 */
@Entity
@Table(name = "inventories", schema = "inventory")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inventory extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "product_sku_id", nullable = false)
    private UUID productSkuId;

    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity;

    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity;

    @Column(name = "reorder_level", nullable = false)
    private Integer reorderLevel;
}
