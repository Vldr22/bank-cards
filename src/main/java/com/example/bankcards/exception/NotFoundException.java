package com.example.bankcards.exception;

import com.example.bankcards.constants.ErrorMessages;

public class NotFoundException extends BaseCustomException {

    public NotFoundException(String message, String identifier) {
        super(message, identifier);
    }

    public static NotFoundException userById(Long id) {
        return new NotFoundException(
                String.format("%s, %d", ErrorMessages.USER_NOT_FOUND, id),
                String.valueOf(id)
        );
    }

    public static NotFoundException userByEmail(String email) {
        return new NotFoundException(
                String.format("%s, %s", ErrorMessages.USER_NOT_FOUND, email),
                email
        );
    }

    public static NotFoundException roleByName(String name) {
        return new NotFoundException(
                String.format("%s, %s", ErrorMessages.ROLE_NOT_FOUND, name),
                name
        );
    }

    public static NotFoundException cardById(Long id) {
        return new NotFoundException(
                String.format("%s, %d", ErrorMessages.USER_NOT_FOUND, id),
                String.valueOf(id)
        );
    }

    public static NotFoundException blockOrderByCardId(Long id) {
        return new NotFoundException(
                String.format("%s, %d", ErrorMessages.BLOCK_ORDER_NOT_FOUND, id),
                String.valueOf(id)
        );
    }

}
