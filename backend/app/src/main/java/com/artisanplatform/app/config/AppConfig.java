package com.artisanplatform.app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Application-wide bean definitions. Domain-module-specific configuration
 * (e.g. the {@code ai} module's provider adapter, the {@code payment}
 * module's gateway adapter) lives inside those modules, not here — this
 * class only holds beans genuinely shared across the whole application.
 */
@Configuration
public class AppConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    /**
     * Shared HTTP client for outbound calls this application makes to
     * external systems (AI providers, payment gateway) — used by the
     * provider-agnostic adapters in the {@code ai} and {@code payment}
     * modules per architecture principle #14 (isolate external
     * integrations behind service interfaces/adapters).
     */
    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}
