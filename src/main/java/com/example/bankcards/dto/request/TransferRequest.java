package com.example.bankcards.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequest(
        @NotNull(message = "ID карты отправителя не может быть пустым")
        Long fromCardId,

        @NotNull(message = "ID карты получателя не может быть пустым")
        Long toCardId,

        @NotNull(message = "Сумма не может быть пустой")
        @DecimalMin(value = "0.01", message = "Сумма должна быть больше нуля")
        @Digits(integer = 19, fraction = 2, message = "Некорректный формат суммы. 19 цифр и 2 знака после запятой")
        BigDecimal amount
) {
}
