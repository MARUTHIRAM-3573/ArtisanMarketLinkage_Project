package com.artisanplatform.seller.repository;

import com.artisanplatform.seller.domain.entity.Seller;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerRepository extends JpaRepository<Seller, UUID> {

    java.util.List<Seller> findByUserId(java.util.UUID userId);
}
