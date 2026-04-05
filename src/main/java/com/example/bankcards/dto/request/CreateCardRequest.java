package com.example.bankcards.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "Запрос на создание карты")
public record CreateCardRequest(
        @Schema(description = "ID пользователя", example = "1")
        @NotNull(message = "ID пользователя не может быть пустым")
        Long userId,

        @Schema(description = "Имя владельца карты", example = "IVAN IVANOV")
        @NotBlank(message = "Имя владельца не может быть пустым")
        @Size(max = 255, message = "Имя владельца не должно превышать 255 символов")
        String cardHolder,

        @Schema(description = "Начальный баланс", example = "100.00")
        @NotNull(message = "Начальный баланс не может быть пустым")
        @DecimalMin(value = "0.0", message = "Баланс не может быть отрицательным")
        @Digits(integer = 19, fraction = 2, message = "Некорректный формат баланса. Максимум 19 цифр и 2 знака после запятой")
        BigDecimal initialBalance
) {
}
