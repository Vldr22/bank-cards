package com.example.bankcards.dto;

import com.example.bankcards.enums.CommonResponseStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.ProblemDetail;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T>{

    private T data;
    private CommonResponseStatus status;
    private ProblemDetail problemDetail;
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