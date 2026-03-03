package com.group1.franchiseservice.service.impl;

import com.group1.franchiseservice.common.security.JwtProperties;
import com.group1.franchiseservice.common.security.JwtService;
import com.group1.franchiseservice.common.security.RefreshTokenStore;
import com.group1.franchiseservice.model.dto.LoginRequest;
import com.group1.franchiseservice.model.dto.LoginResponse;
import com.group1.franchiseservice.model.dto.RegisterRequest;
import com.group1.franchiseservice.model.entity.Account;
import com.group1.franchiseservice.model.entity.Role;
import com.group1.franchiseservice.repository.UserRepository;
import com.group1.franchiseservice.service.AuthService;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {

    AuthenticationManager authenticationManager;
    JwtService jwtService;
    RefreshTokenStore refreshTokenStore;;
    UserRepository userRepository;
    JwtProperties props;
    PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        Account user = (Account) auth.getPrincipal();

        String access = jwtService.generateAccessToken(user);

        String jti = UUID.randomUUID().toString();
        String refresh = jwtService.generateRefreshToken(user, jti);

        refreshTokenStore.store(
                jti,
                user.getId().toString(),
                Duration.ofDays(props.refreshTokenExpDays())
        );

        return new LoginResponse(access, refresh);
    }

    @Override
    public void logout(String refreshToken) {

        if (refreshToken == null || refreshToken.isBlank()) return;

        try {
            var claims = jwtService.parseClaims(refreshToken);

            if (!"refresh".equals(claims.get("typ", String.class))) return;

            String jti = claims.getId();
            if (jti != null) {
                refreshTokenStore.revoke(jti);
            }

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            var claims = e.getClaims();
            if (claims != null && claims.getId() != null) {
                refreshTokenStore.revoke(claims.getId());
            }
        } catch (Exception ignored) {
            // token fake hoặc sai format → coi như logout thành công
        }
    }

    public LoginResponse refresh(String refreshToken) {

        Claims claims = jwtService.parseClaims(refreshToken);

        if (!"refresh".equals(claims.get("typ", String.class))) {
            throw new RuntimeException("Invalid token type");
        }

        String jti = claims.getId();

        refreshTokenStore.getUserIdByJti(jti)
                .orElseThrow(() -> new RuntimeException("Refresh token revoked"));

        String username = claims.getSubject();

        Account user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newAccess = jwtService.generateAccessToken(user);

        return new LoginResponse(newAccess, refreshToken);
    }

    @Override
    public void register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Username already exists");
        }

        if (request.role() == null || request.role().isBlank()) {
            throw new RuntimeException("Role is required");
        }

        Role roleToAssign;

        try {
            roleToAssign = Role.valueOf(request.role().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role");
        }

        Account account = new Account();
        account.setUsername(request.username());
        account.setPassword(passwordEncoder.encode(request.password()));
        account.setRole(roleToAssign);

        userRepository.save(account);
    }

}

