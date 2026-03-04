package service.CSFC.CSFC_auth_service.common.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import service.CSFC.CSFC_auth_service.common.security.AuthorizationFilter;
import service.CSFC.CSFC_auth_service.common.security.CustomerUserDetailsService;

import java.util.List;

@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthorizationFilter authorizationFilter) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(request -> {
                    request.requestMatchers(
                                    "/auth/register",
                                    "/auth/login",
                                    "/auth/refresh",
                                    "/auth/forgot-password",
                                    "/auth/reset-password"
                            ).permitAll()

                            .anyRequest().authenticated();
                })
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, CustomerUserDetailsService customerUserDetailsService) throws Exception {
        AuthenticationManagerBuilder authBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authBuilder
                .userDetailsService(customerUserDetailsService)
                .passwordEncoder(passwordEncoder());

        return authBuilder.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Cho phép Frontend ở cổng 5173 truy cập
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        // Cho phép các HTTP method này
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // Cho phép các header cần thiết
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "x-no-retry"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Áp dụng cấu hình CORS này cho toàn bộ API (/**)
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
