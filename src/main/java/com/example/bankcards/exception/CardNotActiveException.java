package com.example.bankcards.exception;

import com.example.bankcards.constants.ErrorMessages;

public class CardNotActiveException extends BaseCustomException {

    public CardNotActiveException(String message, String identifier) {
        super(message, identifier);
    }

    public static CardNotActiveException cardById(Long id) {
        return new CardNotActiveException(
                String.format("%s, %d", ErrorMessages.CARD_NOT_ACTIVE, id),
                String.valueOf(id)
        );
    }
}
