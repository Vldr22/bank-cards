package com.example.bankcards.dto.response;

import com.example.bankcards.enums.UserRole;
import com.example.bankcards.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Информация о пользователе")
public record UserResponse(
        @Schema(description = "ID пользователя", example = "1")
        Long id,

        @Schema(description = "Email пользователя", example = "user@gmail.com")
        String email,

        @Schema(description = "Статус пользователя", example = "ACTIVE")
        UserStatus status,

        @Schema(description = "Роль пользователя", example = "ROLE_USER")
        UserRole role,

        @Schema(description = "Дата регистрации", example = "2026-05-03T22:18:39")
        LocalDateTime createdAt
) {
}
