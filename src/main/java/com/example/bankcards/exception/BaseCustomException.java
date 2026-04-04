package com.example.bankcards.exception;

import lombok.Getter;

@Getter
public class BaseCustomException extends RuntimeException {

    private final String identifier;

    public BaseCustomException(String message, String identifier) {
        super(message);
        this.identifier = identifier;
    }
}
