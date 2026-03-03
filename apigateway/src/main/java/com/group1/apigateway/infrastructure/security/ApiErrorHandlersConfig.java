package com.group1.apigateway.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.group1.apigateway.common.response.ApiResponse;
import com.group1.apigateway.model.dto.ApiError;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Configuration
public class ApiErrorHandlersConfig {

    @Value("${spring.application.name:api-gateway}")
    private String serviceName;

    @Value("${app.version:1.0.0}")
    private String version;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        return om;
    }

    // 401 – JWT invalid / expired / missing (khi endpoint yêu cầu authenticated)
    @Bean
    public ServerAuthenticationEntryPoint authenticationEntryPoint(ObjectMapper om) {
        return (exchange, ex) -> writeApiError(
                exchange, om,
                HttpStatus.UNAUTHORIZED,
                "UNAUTHORIZED",
                "Invalid or expired token"
        );
    }

    // 403 – token hợp lệ nhưng không đủ quyền (để sẵn, sau này dùng authorization)
    @Bean
    public ServerAccessDeniedHandler accessDeniedHandler(ObjectMapper om) {
        return (exchange, ex) -> writeApiError(
                exchange, om,
                HttpStatus.FORBIDDEN,
                "FORBIDDEN",
                "Access denied"
        );
    }

    private Mono<Void> writeApiError(ServerWebExchange exchange, ObjectMapper om,
                                     HttpStatus status, String code, String message) {

        var response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ApiResponse<Void> body = ApiResponse.<Void>builder()
                .serviceName(serviceName)
                .version(version)
                .requestId(exchange.getRequest().getId())
                .timestamp(Instant.now())
                .error(ApiError.builder()
                        .code(code)
                        .message(message)
                        .build())
                .build();

        byte[] bytes;
        try {
            bytes = om.writeValueAsBytes(body);
        } catch (Exception e) {
            bytes = ("{\"serviceName\":\"" + serviceName + "\",\"version\":\"" + version
                    + "\",\"requestId\":\"unknown\",\"error\":{\"code\":\"SERIALIZE_ERROR\","
                    + "\"message\":\"serialize failed\"}}")
                    .getBytes(StandardCharsets.UTF_8);
        }

        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }
}
