package com.artisanplatform.pricing.repository;

import com.artisanplatform.pricing.domain.entity.SkuPrice;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkuPriceRepository extends JpaRepository<SkuPrice, UUID> {

    java.util.List<SkuPrice> findByProductSkuId(java.util.UUID productSkuId);
}
