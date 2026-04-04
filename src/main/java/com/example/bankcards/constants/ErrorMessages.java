package com.example.bankcards.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorMessages {

    // UsER
    public static final String USER_NOT_FOUND = "User not found";
    public static final String USER_ALREADY_EXISTS = "User already exists";
    public static final String USER_BLOCKED = "User is blocked";
    public static final String USER_DELETED = "User is deleted";

    //ROLE
    public static final String ROLE_NOT_FOUND = "Role not found";

    // GENERAL
    public static final String DATA_INTEGRITY_ERROR = "Data integrity violation";
    public static final String UNEXPECTED_ERROR = "Unexpected error";
}
