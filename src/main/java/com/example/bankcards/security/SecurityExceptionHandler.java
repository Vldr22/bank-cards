package com.example.bankcards.security;

import com.example.bankcards.constants.SecurityErrorMessages;
import com.example.bankcards.util.SecurityResponseUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityExceptionHandler implements AccessDeniedHandler, AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        log.warn("Authentication error: {}", authException.getMessage());
        SecurityResponseUtils.sendError(
                response,
                objectMapper,
                HttpStatus.UNAUTHORIZED,
                SecurityErrorMessages.AUTHENTICATION_REQUIRED
        );

    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        log.warn("Access Denied: {}", accessDeniedException.getMessage());
        SecurityResponseUtils.sendError(
                response,
                objectMapper,
                HttpStatus.FORBIDDEN,
                SecurityErrorMessages.ACCESS_DENIED
        );
    }
}
