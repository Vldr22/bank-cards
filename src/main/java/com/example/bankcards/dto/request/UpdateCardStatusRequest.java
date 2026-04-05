package com.example.bankcards.dto.request;

import com.example.bankcards.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Запрос на изменение статуса карты")
public record UpdateCardStatusRequest(
        @Schema(description = "Новый статус карты", example = "BLOCKED")
        @NotNull(message = "Обновленный статус для карты не может быть пуст")
        CardStatus status
) {
}
