package com.example.bankcards.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Запрос на перевод между картами")
public record TransferRequest(
        @Schema(description = "ID карты с которой будут отправлены средства", example = "1")
        @NotNull(message = "ID карты отправителя не может быть пустым")
        Long fromCardId,

        @Schema(description = "ID карты на которую будут получены средства", example = "2")
        @NotNull(message = "ID карты получателя не может быть пустым")
        Long toCardId,

        @Schema(description = "Сумма перевода", example = "100.00")
        @NotNull(message = "Сумма не может быть пустой")
        @DecimalMin(value = "0.01", message = "Сумма должна быть больше нуля")
        @Digits(integer = 19, fraction = 2, message = "Некорректный формат суммы. 19 цифр и 2 знака после запятой")
        BigDecimal amount
) {
}
