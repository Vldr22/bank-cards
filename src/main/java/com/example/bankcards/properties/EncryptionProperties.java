package com.example.bankcards.properties;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "app.encryption")
public class EncryptionProperties {

    @NotNull(message = "Encryption key is required")
    private final String key;

    @NotNull(message = "Encryption salt is required")
    private final String salt;

}
