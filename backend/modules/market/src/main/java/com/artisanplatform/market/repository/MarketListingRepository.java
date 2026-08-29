package com.artisanplatform.market.repository;

import com.artisanplatform.market.domain.entity.MarketListing;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketListingRepository extends JpaRepository<MarketListing, UUID> {

    java.util.List<MarketListing> findByProductId(java.util.UUID productId);
}
