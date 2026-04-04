package com.example.bankcards.exception;

import com.example.bankcards.constants.ErrorMessages;

public class AlreadyExistsException extends BaseCustomException {

    public AlreadyExistsException(String message, String identifier) {
        super(message, identifier);
    }

    public static AlreadyExistsException userByEmail(String email) {
        return new AlreadyExistsException(
                String.format("%s, %s", ErrorMessages.USER_ALREADY_EXISTS, email),
                email
        );
    }


}
