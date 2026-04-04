package com.example.bankcards.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateCardRequest(
        @NotNull(message = "ID пользователя не может быть пустым")
        Long userId,

        @NotBlank(message = "Имя владельца не может быть пустым")
        @Size(max = 255, message = "Имя владельца не должно превышать 255 символов")
        String cardHolder,

        @NotNull(message = "Начальный баланс не может быть пустым")
        @DecimalMin(value = "0.0", message = "Баланс не может быть отрицательным")
        @Digits(integer = 19, fraction = 2, message = "Некорректный формат баланса. Максимум 19 цифр и 2 знака после запятой")
        BigDecimal initialBalance
) {
}
