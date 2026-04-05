package com.example.bankcards.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
@Schema(description = "Регистрация пользователя в системе")
public record RegisterRequest(
        @Schema(description = "Email пользователя", example = "user@gmail.com")
        @NotBlank(message = "Email не может быть пустым")
        @Email(message = "Некорректный формат email")
        String email,

        @Schema(description = "Пароль", example = "password123")
        @NotBlank(message = "Пароль не может быть пустым")
        @Size(min = 8, max = 32, message = "Пароль должен содержать от 8 до 32 символов")
        String password
) {
}
