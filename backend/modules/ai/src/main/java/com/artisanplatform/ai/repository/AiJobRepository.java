package com.artisanplatform.ai.repository;

import com.artisanplatform.ai.domain.entity.AiJob;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiJobRepository extends JpaRepository<AiJob, UUID> {

    java.util.List<AiJob> findByRequestedByUserId(java.util.UUID requestedByUserId);
}
