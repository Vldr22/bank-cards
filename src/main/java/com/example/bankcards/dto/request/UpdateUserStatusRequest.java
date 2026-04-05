package com.example.bankcards.dto.request;

import com.example.bankcards.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Запрос на изменение статуса пользователя")
public record UpdateUserStatusRequest(
        @Schema(description = "Новый статус пользователя", example = "BLOCKED")
        @NotNull(message = "Обновленный статус для пользователя не может быть пустым")
        UserStatus userStatus
) {
}
