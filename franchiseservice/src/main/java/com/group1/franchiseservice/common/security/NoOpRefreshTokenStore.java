package com.group1.franchiseservice.common.security;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@Profile("dev")
public class NoOpRefreshTokenStore implements RefreshTokenStore {

    @Override
    public void store(String jti, String userId, Duration ttl) {
        // Dev mode: không lưu gì
    }

    @Override
    public Optional<String> getUserIdByJti(String jti) {
        return Optional.of("dev");
    }

    @Override
    public void revoke(String jti) {
        // Dev mode: không làm gì
    }
}