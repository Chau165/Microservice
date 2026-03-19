package com.group1.app.common.security;

import java.util.List;

public class UserPrincipal {

    private final String userId;
    private final String name;
    private final List<String> permissions;

    public UserPrincipal(String userId, String name, List<String> permissions) {
        this.userId = userId;
        this.name = name;
        this.permissions = permissions;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public List<String> getPermissions() {
        return permissions;
    }
}