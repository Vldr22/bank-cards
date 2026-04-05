package com.example.bankcards.dto;

import com.example.bankcards.enums.CommonResponseStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.http.ProblemDetail;

import java.time.LocalDateTime;

@Schema(description = "Общий формат ответа API")
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T>{

    @Schema(description = "Данные ответа")
    private T data;

    @Schema(description = "Статус ответа", example = "SUCCESS")
    private CommonResponseStatus status;

    @Schema(description = "Детали ошибки (только при ERROR)")
    private ProblemDetail problemDetail;

    @Schema(description = "Время ответа", example = "2026-04-03T22:18:39")
    private LocalDateTime timestamp;

    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(
                data,
                CommonResponseStatus.SUCCESS,
                null,
                LocalDateTime.now());
    }

    public static <T> CommonResponse<T> error(ProblemDetail problemDetail) {
        return new CommonResponse<>(
                null,
                CommonResponseStatus.ERROR,
                problemDetail,
                LocalDateTime.now());
    }
}