package com.example.bankcards.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";
    private static final String API_TITLE = "Bank Cards API";
    private static final String API_VERSION = "1.0.0";
    private static final String CONTACT_NAME = "Vladimir - GitHub";
    private static final String CONTACT_URL = "https://github.com/Vldr22/bank-rest";
    private static final String DESCRIPTION_PATH = "/swagger-description.md";
    private static final String JWT_DESCRIPTION = "JWT токен, полученный через POST /api/auth/login";


    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(API_TITLE)
                        .version(API_VERSION)
                        .description(loadDescription())
                        .contact(new Contact()
                                .name(CONTACT_NAME)
                                .url(CONTACT_URL)))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description(JWT_DESCRIPTION)))
                .tags(List.of(
                        new Tag().name("Auth").description("Регистрация и вход в систему"),
                        new Tag().name("Cards").description("Управление своими картами и переводы"),
                        new Tag().name("Admin").description("Администрирование карт, пользователей и заявок на блокировку")
                ));
    }

    private String loadDescription() {
        InputStream is = getClass().getResourceAsStream(DESCRIPTION_PATH);
        if (is == null) {
            return API_TITLE;
        }
        try (is) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return API_TITLE;
        }
    }

}

