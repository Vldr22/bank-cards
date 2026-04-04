package com.example.bankcards.security;

import com.example.bankcards.constants.SecurityConstants;
import com.example.bankcards.constants.SecurityErrorMessages;
import com.example.bankcards.util.SecurityResponseUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();
        return Arrays.stream(SecurityConstants.PUBLIC_PATHS).anyMatch(requestURI::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Optional<String> token = jwtTokenService.extractTokenFromRequest(request);

        if (token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String subject = jwtTokenService.extractSubject(token.get());
            String role = jwtTokenService.extractRole(token.get());
            setAuthentication(subject, role);

        } catch (ExpiredJwtException e) {
            log.debug("Expired JWT token for request {}", request.getRequestURI());
            sendError(response, SecurityErrorMessages.EXPIRED_TOKEN);
            return;

        } catch (Exception e) {
            log.warn("JWT token for request {} failed. Reason: {}", request.getRequestURI(), e.getMessage());
            sendError(response, SecurityErrorMessages.INVALID_TOKEN);
            return;
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private void setAuthentication(String subject, String role) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        subject,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority(role))
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("Authenticated: subject={}, role={}", subject, role);
    }

    private void sendError(HttpServletResponse httpResponse, String message) throws IOException {
        SecurityContextHolder.clearContext();
        SecurityResponseUtils.sendError(
                httpResponse,
                objectMapper,
                HttpStatus.UNAUTHORIZED,
                message
        );
    }

}
