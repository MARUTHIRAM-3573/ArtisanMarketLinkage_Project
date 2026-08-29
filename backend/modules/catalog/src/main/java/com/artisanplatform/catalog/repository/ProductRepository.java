package com.artisanplatform.catalog.repository;

import com.artisanplatform.catalog.domain.entity.Product;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    java.util.List<Product> findBySellerId(java.util.UUID sellerId);
}
