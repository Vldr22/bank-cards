package com.example.bankcards.dto.request;

import com.example.bankcards.enums.UserStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusRequest(
        @NotNull(message = "Обновленный статус для пользователя не может быть пустым")
        UserStatus userStatus
) {
}
