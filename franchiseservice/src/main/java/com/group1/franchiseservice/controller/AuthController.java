package com.group1.franchiseservice.controller;

import com.group1.franchiseservice.common.security.JwtProperties;
import com.group1.franchiseservice.model.dto.LoginRequest;
import com.group1.franchiseservice.model.dto.LoginResponse;
import com.group1.franchiseservice.model.dto.RegisterRequest;
import com.group1.franchiseservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtProperties jwtProperties;

    @Value("${app.cookie.secure}")
    private boolean cookieSecure;

    private static final String ACCESS_COOKIE = "access_token";
    private static final String REFRESH_COOKIE = "refresh_token";

    // ===================== LOGIN =====================
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {

        LoginResponse tokens = authService.login(request);

        ResponseCookie accessCookie = ResponseCookie.from(ACCESS_COOKIE, tokens.accessToken())
                .httpOnly(true)
                .secure(cookieSecure) // đổi true khi deploy HTTPS
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofMinutes(jwtProperties.accessTokenExpMinutes()))
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_COOKIE, tokens.refreshToken())
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Strict")
                .path("/api/auth")
                .maxAge(Duration.ofDays(jwtProperties.refreshTokenExpDays()))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(tokens);
    }

    // ===================== REFRESH =====================
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken) {

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new RuntimeException("Refresh token is missing");
        }

        LoginResponse tokens = authService.refresh(refreshToken);

        ResponseCookie accessCookie = ResponseCookie.from(ACCESS_COOKIE, tokens.accessToken())
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofMinutes(jwtProperties.accessTokenExpMinutes()))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .body(tokens);
    }

    // ===================== LOGOUT =====================
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken) {

        if (refreshToken != null && !refreshToken.isBlank()) {
            authService.logout(refreshToken);
        }

        ResponseCookie clearAccess = ResponseCookie.from(ACCESS_COOKIE, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie clearRefresh = ResponseCookie.from(REFRESH_COOKIE, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Strict")
                .path("/api/auth")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearAccess.toString())
                .header(HttpHeaders.SET_COOKIE, clearRefresh.toString())
                .build();
    }

    // ===================== REGISTER =====================
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok().build();
    }


}