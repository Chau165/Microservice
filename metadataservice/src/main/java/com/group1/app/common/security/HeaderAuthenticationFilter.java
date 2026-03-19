package com.group1.app.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Component
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_ROLE = "X-User-Role";
    private static final String HEADER_USER_NAME = "X-User-Name";
    private static final String HEADER_USER_PERMISSIONS = "X-User-Permissions";

    private static final Logger log = LoggerFactory.getLogger(HeaderAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String userId = request.getHeader(HEADER_USER_ID);
        String role = request.getHeader(HEADER_USER_ROLE);
        String name = request.getHeader(HEADER_USER_NAME);
        String permissionsStr = request.getHeader(HEADER_USER_PERMISSIONS);

        log.debug("Incoming headers: {}={}, {}={}, {}={}, perms={}",
                HEADER_USER_ID, userId,
                HEADER_USER_ROLE, role,
                HEADER_USER_NAME, name,
                permissionsStr
        );

        if (userId != null && role != null) {

            Set<GrantedAuthority> authorities = new HashSet<>();
            Set<String> permissionSet = new HashSet<>();

            // 1. ROLE
            String roleAuthority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
            authorities.add(new SimpleGrantedAuthority(roleAuthority));

            // 2. PERMISSIONS
            if (permissionsStr != null && !permissionsStr.isBlank()) {
                String[] permissions = permissionsStr.split(",");
                for (String permission : permissions) {
                    String clean = permission == null ? "" : permission.trim().toUpperCase();
                    if (!clean.isEmpty()) {
                        permissionSet.add(clean);
                        authorities.add(new SimpleGrantedAuthority(clean));
                    }
                }
            }

            // 3. SAFE NAME
            String safeName = (name != null && !name.isBlank()) ? name : "unknown";

            // 4. PRINCIPAL
            UserPrincipal principal = new UserPrincipal(
                    userId,
                    safeName,
                    new ArrayList<>(permissionSet)
            );

            // 5. AUTHENTICATION
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            authorities
                    );

            // defensive clear
            SecurityContextHolder.clearContext();
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("Set authentication for userId={}, role={}, permsCount={}",
                    userId,
                    roleAuthority,
                    permissionSet.size()
            );
        }

        filterChain.doFilter(request, response);
    }
}