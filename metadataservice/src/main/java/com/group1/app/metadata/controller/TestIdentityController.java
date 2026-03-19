package com.group1.app.metadata.controller;

import com.group1.app.common.security.UserPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestIdentityController {

    @GetMapping("/api/metadata/public/test/identity")
    public Map<String, Object> testIdentity() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        UserPrincipal user =
                (UserPrincipal) authentication.getPrincipal();

        Map<String, Object> result = new HashMap<>();

        result.put("userId", user.getUserId());
        result.put("name", user.getName());
        result.put("role", authentication.getAuthorities());

        return result;
    }
}
