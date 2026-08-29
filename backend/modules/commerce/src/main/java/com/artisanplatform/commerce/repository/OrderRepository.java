package com.artisanplatform.commerce.repository;

import com.artisanplatform.commerce.domain.entity.Order;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    java.util.List<Order> findByUserId(java.util.UUID userId);
}
