package service.CSFC.CSFC_auth_service.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.function.Function;

public interface JwtService {
    String generateAccessToken(UserDetails userDetails);

    String generateRefreshToken(UserDetails userDetails);

    String generatePasswordResetToken(String email);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    String extractUsername(String token);

    Date extractExpiration(String token);

    boolean isTokenValid(String token, UserDetails userDetails);
}
