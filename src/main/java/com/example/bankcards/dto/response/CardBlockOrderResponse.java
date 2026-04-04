package com.example.bankcards.dto.response;

import com.example.bankcards.enums.BlockOrderStatus;

import java.time.LocalDateTime;

public record CardBlockOrderResponse(
        Long id,
        Long cardId,
        String maskedCardNumber,
        Long userId,
        String userEmail,
        BlockOrderStatus status,
        LocalDateTime createdAt
) {
}
