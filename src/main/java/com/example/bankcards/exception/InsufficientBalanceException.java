package com.example.bankcards.exception;

import com.example.bankcards.constants.ErrorMessages;

public class InsufficientBalanceException extends BaseCustomException {

    public InsufficientBalanceException(String message, String identifier) {
        super(message, identifier);
    }

    public static InsufficientBalanceException byCardId(Long cardId) {
        return new InsufficientBalanceException(
                ErrorMessages.INSUFFICIENT_FUNDS,
                String.valueOf(cardId)
        );
    }
}
