package com.example.bankcards.security;

import com.example.bankcards.constants.SecurityConstants;
import com.example.bankcards.constants.SecurityErrorMessages;
import com.example.bankcards.enums.UserRole;
import com.example.bankcards.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@EnableConfigurationProperties(JwtProperties.class)
public class JwtTokenService {

    private final SecretKey key;

    @Getter
    private final long expirationSeconds;

    public JwtTokenService(JwtProperties jwtProperties) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());

        if (keyBytes.length < SecurityConstants.MIN_JWT_KEY_LENGTH) {
            throw new SecurityException(SecurityErrorMessages.WEAK_KEY);
        }

        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationSeconds = jwtProperties.getExpiration();
    }

    public String generateToken(String subject, UserRole role) {
        Instant now = Instant.now();
        Instant expiration = now.plus(expirationSeconds, ChronoUnit.SECONDS);

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .claim(SecurityConstants.CLAIM_ROLE, role.name())
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(key)
                .compact();
    }

    public String extractSubject(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractClaims(token).get(SecurityConstants.CLAIM_ROLE, String.class);
    }

    public Optional<String> extractTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);
        if (header != null && header.startsWith(SecurityConstants.BEARER_PREFIX)) {
            return Optional.of(header.substring(SecurityConstants.BEARER_PREFIX.length()));
        }
        return Optional.empty();
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
