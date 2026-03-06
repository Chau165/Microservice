package service.CSFC.CSFC_auth_service.service.imp;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import service.CSFC.CSFC_auth_service.common.security.CustomerUserDetails;
import service.CSFC.CSFC_auth_service.common.security.JwtProperties;
import service.CSFC.CSFC_auth_service.service.JwtService;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtServiceImp implements JwtService {

    private final JwtProperties jwtProperties;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    public void init() {
        try {
            this.privateKey = loadPrivateKey(jwtProperties.privateKey());
            this.publicKey = loadPublicKey(jwtProperties.publicKey());
        } catch (Exception e) {
            throw new RuntimeException("Cannot initialize RSA keys", e);
        }
    }

    private PrivateKey loadPrivateKey(String keyString) throws Exception {
        String key = keyString.replaceAll("\\s+", "");
        byte[] keyBytes = Base64.getDecoder().decode(key);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
    }

    private PublicKey loadPublicKey(String keyString) throws Exception {
        String key = keyString.replaceAll("\\s+", "");
        byte[] keyBytes = Base64.getDecoder().decode(key);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration,
            String tokenType
    ) {

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .claim("tokenType", tokenType)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(privateKey)
                .compact();
    }

    @Override
    public String generateAccessToken(UserDetails userDetails) {

        Map<String, Object> extraClaims = new HashMap<>();

        extraClaims.put("roles",
                userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
        );

        if (userDetails instanceof CustomerUserDetails customUserDetails) {
            extraClaims.put("userId", customUserDetails.getUser().getId());
        }

        return buildToken(
                extraClaims,
                userDetails,
                jwtProperties.accessTokenExpiration(),
                "access"
        );
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {

        Map<String, Object> extraClaims = new HashMap<>();

        if (userDetails instanceof CustomerUserDetails customUserDetails) {
            extraClaims.put("userId", customUserDetails.getUser().getId());
        }

        return buildToken(
                extraClaims,
                userDetails,
                jwtProperties.refreshTokenExpiration(),
                "refresh"
        );
    }

    @Override
    public String generatePasswordResetToken(String email) {
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(email)
                .claim("tokenType", "reset")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(privateKey)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("tokenType", String.class));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {

        final String username = extractUsername(token);
        final String tokenType = extractTokenType(token);

        return username.equals(userDetails.getUsername())
                && "access".equals(tokenType)
                && !isTokenExpired(token);
    }
}