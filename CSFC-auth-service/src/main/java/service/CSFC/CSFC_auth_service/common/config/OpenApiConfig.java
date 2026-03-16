package service.CSFC.CSFC_auth_service.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CSFC Unified API")
                        .version("1.0.0")
                        .description("API tổng hợp cho Authentication Service & Engagement Service")
                        .contact(new Contact()
                                .name("Group 5")
                                .email("support@group5.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080" + contextPath)
                                .description("Local Development Server")
                ))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Nhập JWT token để authenticate")));
    }

    @Bean
    public GroupedOpenApi authenticationGroup() {
        return GroupedOpenApi.builder()
                .group("authentication")
                .displayName("1. Authentication Service")
                .pathsToMatch(
                        "/auth/**",
                        "/users/**",
                        "/roles/**",
                        "/admin/roles/**",
                        "/api/admin/auth-users/**"
                )
                .build();
    }

    @Bean
    public GroupedOpenApi engagementGroup() {
        return GroupedOpenApi.builder()
                .group("engagement")
                .displayName("2. Engagement Service")
                .pathsToMatch(
                        "/api/engagement/**",
                        "/engagement/**"
                )
                .build();
    }

    @Bean
    public GroupedOpenApi allApisGroup() {
        return GroupedOpenApi.builder()
                .group("all")
                .displayName("0. All APIs")
                .pathsToMatch("/**")
                .build();
    }
}
