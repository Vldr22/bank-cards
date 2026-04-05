package com.example.bankcards.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с JWT-token")
public record AuthResponse(
        @Schema(description = "JWT токен", example = "eyJhbGciOiJIUzM4NCJ9...")
        String token
) {
}
