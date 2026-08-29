-- Inventory schema: current stock state + immutable movement ledger.
-- Source: docs/architecture/04_DATA_MODEL_AND_OWNERSHIP.md §2.5
-- Owning module: backend/modules/inventory

CREATE SCHEMA IF NOT EXISTS inventory;

CREATE TABLE inventory.inventories (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_sku_id        UUID NOT NULL REFERENCES catalog.product_skus(id) ON DELETE CASCADE,
    available_quantity    INTEGER NOT NULL DEFAULT 0,
    reserved_quantity     INTEGER NOT NULL DEFAULT 0,
    reorder_level         INTEGER NOT NULL DEFAULT 0,
    created_at            TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at            TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_inventories_sku_id UNIQUE (product_sku_id),
    CONSTRAINT chk_inventories_available_nonneg CHECK (available_quantity >= 0),
    CONSTRAINT chk_inventories_reserved_nonneg CHECK (reserved_quantity >= 0)
);

CREATE TABLE inventory.inventory_movements (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    inventory_id     UUID NOT NULL REFERENCES inventory.inventories(id) ON DELETE CASCADE,
    movement_type    VARCHAR(16) NOT NULL,
    quantity         INTEGER NOT NULL,
    reference_type   VARCHAR(32),
    reference_id     UUID,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_inventory_movements_type CHECK (
        movement_type IN ('STOCK_IN','SALE','RESERVATION','RELEASE','RETURN','ADJUSTMENT','DAMAGE')
    )
);
CREATE INDEX idx_inventory_movements_inventory_id_created_at ON inventory.inventory_movements(inventory_id, created_at);
