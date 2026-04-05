package com.example.bankcards.dto.response;

import com.example.bankcards.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Информация о банковской карте для пользователя")
public record CardResponse(
        @Schema(description = "Маскированный номер карты", example = "**** **** **** 1234")
        String maskedNumber,

        @Schema(description = "Имя владельца", example = "IVAN IVANOV")
        String cardHolder,

        @Schema(description = "Срок действия", example = "2030-05-03")
        LocalDate expiresAt,

        @Schema(description = "Статус карты", example = "ACTIVE")
        CardStatus status,

        @Schema(description = "Баланс", example = "100.00")
        BigDecimal balance
) {
}
