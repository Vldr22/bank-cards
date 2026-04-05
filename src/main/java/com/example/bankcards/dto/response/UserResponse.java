package com.example.bankcards.dto.response;

import com.example.bankcards.enums.UserRole;
import com.example.bankcards.enums.UserStatus;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String email,
        UserStatus status,
        UserRole role,
        LocalDateTime createdAt
) {
}
