package com.group4.shift_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Shift Service API")
                        .version("1.0.0")
                        .description("Tài liệu API cho Module Quản lý Ca làm việc (Shift Service)"))
                // Cấu hình để Swagger UI hiển thị ô nhập Header "USER"
                .components(new Components()
                        .addSecuritySchemes("USER-Header", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("USER")))
                // Áp dụng yêu cầu Header này cho toàn bộ các API
                .addSecurityItem(new SecurityRequirement().addList("USER-Header"));
    }
}