package com.group1.app.common.security;

import java.security.Principal;

public class UserPrincipal implements Principal {

    private final String userId;
    private final String name;

    public UserPrincipal(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String getName() {
        return name;
    }
}