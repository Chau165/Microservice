package com.group1.apigateway.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import reactor.core.publisher.Mono;

@Configuration
public class JwtAuthConverterConfig {

    /**
     * Cấu hình converter để đọc claim "roles" từ JWT của AUTH-SERVICE
     * thay vì "scope"/"scp" mặc định của Spring.
     *
     * AUTH-SERVICE đặt roles dạng "ROLE_ADMIN", "ROLE_USER" nên
     * không cần thêm prefix.
     */
    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter gac = new JwtGrantedAuthoritiesConverter();
        gac.setAuthoritiesClaimName("roles");
        gac.setAuthorityPrefix(""); // roles đã có prefix ROLE_ rồi

        JwtAuthenticationConverter jac = new JwtAuthenticationConverter();
        jac.setJwtGrantedAuthoritiesConverter(gac);

        return new ReactiveJwtAuthenticationConverterAdapter(jac);
    }
}
