package com.campusgo.config;

import java.util.List;

public class AuthWhitelist {
    private static final List<String> WHITELIST_PREFIX = List.of(
            "/api/auth/login",
            "/api/auth/admin/login",
            "/api/auth/merchant/login",
            "/api/auth/runner/login",
            "/api/auth/register",
            "/api/auth/refresh",
            "/v3/api-docs",
            "/swagger-ui",
            "/actuator"
    );
    private boolean isWhitelisted(String path) {
        return WHITELIST_PREFIX.stream().anyMatch(path::startsWith);
    }

}
