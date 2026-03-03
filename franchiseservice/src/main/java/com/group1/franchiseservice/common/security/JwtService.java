package com.group1.franchiseservice.common.security;

import com.group1.franchiseservice.model.entity.Account;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
@Data
public class JwtService {
    private final JwtProperties props;
    private PrivateKey privateKey;
    @Getter
    private PublicKey publicKey;

    public JwtService(JwtProperties props) {
        this.props = props;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            // 1. Load Private Key (Dùng để ký Token - PKCS8 Format)
            String rsaPrivateKey = props.privateKey().replaceAll("\\s+", "");
            byte[] privateKeyBytes = Base64.getDecoder().decode(rsaPrivateKey);
            this.privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

            // 2. Load Public Key (Dùng để Verify Token - X509 Format)
            String rsaPublicKey = props.publicKey().replaceAll("\\s+", "");
            byte[] publicKeyBytes = Base64.getDecoder().decode(rsaPublicKey);
            this.publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

        } catch (Exception e) {
            log.error("Failed to load RSA keys. Check your application.yml configuration", e);
            throw new RuntimeException("Could not initialize JWT RSA keys", e);
        }
    }

    // Access Token (Ngắn hạn)
    public String generateAccessToken(Account user) {
        Instant now = Instant.now();
        Instant exp = now.plus(props.accessTokenExpMinutes(), ChronoUnit.MINUTES);

        return Jwts.builder()
                .header()
                .keyId("franchise-key")
                .and()
                .subject(user.getUsername())
                .claims(Map.of(
                        "uid", user.getId().toString(),
                        "role", user.getRole().name(),
                        "typ", "access"
                ))
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(privateKey) // Truyền PrivateKey vào đây, thư viện tự động dùng RS256
                .compact();
    }

    // Refresh Token (Dài hạn)
    public String generateRefreshToken(Account user, String jti) {
        Instant now = Instant.now();
        Instant exp = now.plus(props.refreshTokenExpDays(), ChronoUnit.DAYS);

        return Jwts.builder()
                .header()
                .keyId("franchise-key")
                .and()
                .id(jti)
                .subject(user.getUsername())
                .claims(Map.of(
                        "uid", user.getId().toString(),
                        "typ", "refresh"
                ))
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(privateKey) // Dùng PrivateKey để ký
                .compact();
    }

    // Kiểm tra và lấy thông tin Token
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(publicKey) // Truyền PublicKey vào đây để giải mã
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}