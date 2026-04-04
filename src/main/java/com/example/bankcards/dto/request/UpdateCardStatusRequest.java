package com.example.bankcards.dto.request;

import com.example.bankcards.enums.CardStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateCardStatusRequest(
        @NotNull(message = "Обновленный статус для карты не может быть пуст")
        CardStatus status
) {
}
