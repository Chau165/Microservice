package com.group1.app.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Unified OpenAPI config for the merged Metadata + Shift service.
 * Exposes both Basic-Auth (metadata endpoints) and USER-Header (shift endpoints).
 */
@Configuration
public class OpenApiConfig {

    @Bean
    @Primary
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Metadata & Shift Service API")
                        .version("1.0")
                        .description("Unified API for Metadata Management (Franchise, Contract) and Shift Management"))
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"))
                .addSecurityItem(new SecurityRequirement().addList("USER-Header"))
                .components(new Components()
                        .addSecuritySchemes("basicAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic"))
                        .addSecuritySchemes("USER-Header",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("USER")));
    }
}
