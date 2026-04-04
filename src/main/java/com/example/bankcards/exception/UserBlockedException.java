package com.example.bankcards.exception;

import com.example.bankcards.constants.ErrorMessages;

public class UserBlockedException extends BaseCustomException {

    public UserBlockedException(String message, String identifier) {
        super(message, identifier);
    }

    public static UserBlockedException blockedByEmail(String email) {
        return new UserBlockedException(
                String.format("%s: %s", ErrorMessages.USER_BLOCKED, email),
                email);
    }

    public static UserBlockedException deletedByMail(String email) {
        return new UserBlockedException(
                String.format("%s: %s", ErrorMessages.USER_DELETED, email),
                email
        );
    }
}
