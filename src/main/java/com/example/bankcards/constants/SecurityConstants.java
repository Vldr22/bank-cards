package com.example.bankcards.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SecurityConstants {

    public static final String CLAIM_ROLE = "role";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final int MIN_JWT_KEY_LENGTH = 32;

    public static final String[] PUBLIC_PATHS = {
            "/api/auth/",
            "/swagger-ui/",
            "/v3/api-docs"
    };
}