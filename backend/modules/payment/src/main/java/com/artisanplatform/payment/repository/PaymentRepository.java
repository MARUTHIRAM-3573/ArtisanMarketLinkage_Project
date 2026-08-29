package com.artisanplatform.payment.repository;

import com.artisanplatform.payment.domain.entity.Payment;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    java.util.List<Payment> findByOrderId(java.util.UUID orderId);
}
