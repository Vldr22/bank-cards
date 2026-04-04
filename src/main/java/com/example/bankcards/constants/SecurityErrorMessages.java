package com.example.bankcards.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SecurityErrorMessages {

    public static final String WEAK_KEY = "Key is weak";
    public static final String EXPIRED_TOKEN = "Expired token";
    public static final String INVALID_TOKEN = "Invalid token";
    public static final String INVALID_CREDENTIALS = "Invalid email or password";
    public static final String AUTHENTICATION_REQUIRED = "Authentication required";
    public static final String ACCESS_DENIED = "Access denied";
    public static final String MISSING_AUTHENTICATION_CONTEXT = "Authentication missing security context";

}
