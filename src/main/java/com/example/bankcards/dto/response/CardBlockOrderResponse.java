package com.example.bankcards.dto.response;

import com.example.bankcards.enums.BlockOrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Информация о заявке на блокировку карты")
public record CardBlockOrderResponse(
        @Schema(description = "ID заявки", example = "1")
        Long id,

        @Schema(description = "ID карты", example = "1")
        Long cardId,

        @Schema(description = "Маскированный номер карты", example = "**** **** **** 1234")
        String maskedCardNumber,

        @Schema(description = "ID пользователя", example = "1")
        Long userId,

        @Schema(description = "Email пользователя", example = "user@gmail.com")
        String userEmail,

        @Schema(description = "Статус заявки", example = "PENDING")
        BlockOrderStatus status,

        @Schema(description = "Дата создания заявки", example = "2026-05-03T22:18:39")
        LocalDateTime createdAt
) {
}
