package com.group1.app.common.config;

import com.group1.app.common.security.UserPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.of("SYSTEM");
            }

            // Trích xuất id hoặc name từ UserPrincipal đã được Gateway inject
            if (authentication.getPrincipal() instanceof UserPrincipal user) {
                // Bạn có thể đổi thành user.getName() nếu muốn lưu tên thay vì ID
                return Optional.of(user.getUserId());
            }

            return Optional.of(authentication.getName());
        };
    }
}