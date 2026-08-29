package com.artisanplatform.app.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI bean for the whole application. Every module's controller
 * contributes its endpoints to this single spec (one API surface, one
 * deployable — docs/architecture/05_API_CONTRACTS.md §6).
 */
@Configuration
public class SwaggerConfig {

    private static final String BEARER_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI artisanPlatformOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Artisan Digital Commerce Platform API")
                        .version("v1")
                        .description("AI-enabled digital commerce platform for artisans — B2C, B2B, and Government/institutional channels. "
                                + "See docs/architecture/05_API_CONTRACTS.md for the full contract reference."))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME_NAME, new SecurityScheme()
                                .name(BEARER_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
