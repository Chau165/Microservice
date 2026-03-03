package com.group1.franchiseservice.common.security;

import java.time.Duration;
import java.util.Optional;

public interface RefreshTokenStore {

    void store(String jti, String userId, Duration ttl);

    Optional<String> getUserIdByJti(String jti);

    void revoke(String jti);
}