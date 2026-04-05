package com.example.bankcards.exception;

import com.example.bankcards.constants.ErrorMessages;
import com.example.bankcards.constants.SecurityErrorMessages;
import com.example.bankcards.dto.CommonResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ====== BUSINESS EXCEPTIONS =====
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponse<Void> handleNotFoundException(NotFoundException e) {
        log.warn("Not found: {}, identifier: {}", e.getMessage(), e.getIdentifier());
        return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(UserBlockedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public CommonResponse<Void> handleUserBlocked(UserBlockedException e) {
        log.warn("User blocked: {}, identifier: {}", e.getMessage(), e.getIdentifier());
        return createErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public CommonResponse<Void> handleAlreadyExists(AlreadyExistsException e) {
        log.warn("Already exists: {}, identifier: {}", e.getMessage(), e.getIdentifier());
        return createErrorResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public CommonResponse<Void> handleInsufficientBalance(InsufficientBalanceException e) {
        log.warn("Insufficient balance: {}, identifier: {}", e.getMessage(), e.getIdentifier());
        return createErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
    }

    @ExceptionHandler(CardNotActiveException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public CommonResponse<Void> handleCardNotActive(CardNotActiveException e) {
        log.warn("Card not active: {}, identifier: {}", e.getMessage(), e.getIdentifier());
        return createErrorResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    // ===== FRAMEWORK EXCEPTIONS =====
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Validation failed");
        log.warn("Validation failed: {}", message);
        return createErrorResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<Void> handleConstraintViolation(ConstraintViolationException e) {
        String message = e.getConstraintViolations()
                .stream()
                .findFirst()
                .map(ConstraintViolation::getMessage)
                .orElse("Validation failed");
        log.warn("Constraint violation: {}", message);
        return createErrorResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<Void> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String message = String.format("Parameter '%s' must be of type %s",
                e.getName(), Objects.requireNonNull(e.getRequiredType()).getSimpleName());
        log.warn("Type mismatch: {}", message);
        return createErrorResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public CommonResponse<Void> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.error("Data integrity violation: {}", e.getMessage());
        return createErrorResponse(HttpStatus.CONFLICT, ErrorMessages.DATA_INTEGRITY_ERROR);
    }

    // ===== SECURITY EXCEPTIONS =====
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public CommonResponse<Void> handleBadCredentials() {
        log.warn("Authentication failed: invalid credentials");
        return createErrorResponse(HttpStatus.UNAUTHORIZED, SecurityErrorMessages.INVALID_CREDENTIALS);
    }

    // ===== GENERAL =====
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<Void> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("Illegal argument: {}", e.getMessage());
        return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResponse<Void> handleIllegalState(IllegalStateException e) {
        log.error("Illegal state: {}", e.getMessage());
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.UNEXPECTED_ERROR);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResponse<Void> handleGeneral(Exception e) {
        log.error("Unexpected error occurred", e);
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.UNEXPECTED_ERROR);
    }

    // ====== HELPER =======
    private CommonResponse<Void> createErrorResponse(HttpStatus status, String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        return CommonResponse.error(problemDetail);
    }


}
