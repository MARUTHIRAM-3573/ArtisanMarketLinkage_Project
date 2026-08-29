package com.artisanplatform.inventory.repository;

import com.artisanplatform.inventory.domain.entity.Inventory;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    java.util.List<Inventory> findByProductSkuId(java.util.UUID productSkuId);
}
