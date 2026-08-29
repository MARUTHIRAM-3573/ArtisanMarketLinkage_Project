package com.artisanplatform.b2b.repository;

import com.artisanplatform.b2b.domain.entity.B2bInquiry;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface B2bInquiryRepository extends JpaRepository<B2bInquiry, UUID> {

    java.util.List<B2bInquiry> findByB2bBuyerId(java.util.UUID b2bBuyerId);
}
