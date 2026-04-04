package com.example.bankcards.dto.response;

import com.example.bankcards.enums.CardStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CardResponse(
        String maskedNumber,
        String cardHolder,
        LocalDate expiresAt,
        CardStatus status,
        BigDecimal balance
) {
}
