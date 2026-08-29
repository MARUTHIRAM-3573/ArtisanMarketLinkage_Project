package com.artisanplatform.media.repository;

import com.artisanplatform.media.domain.entity.MediaAsset;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaAssetRepository extends JpaRepository<MediaAsset, UUID> {
}
