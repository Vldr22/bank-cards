package com.example.bankcards.properties;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "app.admin")
public class AdminProperties {

    @NotBlank(message = "Admin email is required")
    @Email(message = "Admin email incorrect format")
    private final String email;

    @NotBlank(message = "Admin password is required")
    private final String password;

}
