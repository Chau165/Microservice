package com.group1.franchiseservice.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@Profile("prod")
@RequiredArgsConstructor
public class RefreshTokenRedisImpl implements RefreshTokenStore {

    private static final String PREFIX = "rt:";
    private final StringRedisTemplate redis;

    @Override
    public void store(String jti, String userId, Duration ttl) {
        redis.opsForValue().set(PREFIX + jti, userId, ttl);
    }

    @Override
    public Optional<String> getUserIdByJti(String jti) {
        return Optional.ofNullable(redis.opsForValue().get(PREFIX + jti));
    }

    @Override
    public void revoke(String jti) {
        redis.delete(PREFIX + jti);
    }
}