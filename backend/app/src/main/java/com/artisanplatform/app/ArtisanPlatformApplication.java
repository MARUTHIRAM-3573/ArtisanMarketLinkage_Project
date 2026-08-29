package com.artisanplatform.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Entry point for the ONE Spring Boot deployable that makes up this
 * platform's backend (modular monolith — docs/architecture/02_ARCHITECTURE_OVERVIEW.md
 * §3, ADR-1). Component-scans, entity-scans, and repository-scans the whole
 * {@code com.artisanplatform} package tree so every domain module's beans,
 * entities, and repositories are picked up without each module needing its
 * own {@code @SpringBootApplication}.
 */
@SpringBootApplication(scanBasePackages = "com.artisanplatform")
@EntityScan(basePackages = "com.artisanplatform")
@EnableJpaRepositories(basePackages = "com.artisanplatform")
@EnableJpaAuditing
public class ArtisanPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArtisanPlatformApplication.class, args);
    }
}
