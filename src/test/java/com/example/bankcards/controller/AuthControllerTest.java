package com.example.bankcards.controller;

import com.example.bankcards.config.TestSecurityConfig;
import com.example.bankcards.dto.request.LoginRequest;
import com.example.bankcards.dto.request.RegisterRequest;
import com.example.bankcards.dto.response.AuthResponse;
import com.example.bankcards.exception.AlreadyExistsException;
import com.example.bankcards.service.facade.AuthFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
@DisplayName("AuthController — endpoints аутентификации")
class AuthControllerTest {

    private static final String TEST_TOKEN = "test.jwt.token";
    private static final String TEST_EMAIL = "user@example.com";
    private static final String TEST_PASSWORD = "TestPassword123!";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthFacade authFacade;

    // ===== POST /api/auth/login =====

    @Test
    @DisplayName("POST /api/auth/login — должен вернуть 200 и токен при верных credentials")
    void login_shouldReturn200AndToken_whenValidCredentials() throws Exception {
        // given
        String requestBody = objectMapper.writeValueAsString(new LoginRequest(TEST_EMAIL, TEST_PASSWORD));
        when(authFacade.login(any())).thenReturn(new AuthResponse(TEST_TOKEN));

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.token").value(TEST_TOKEN));

        verify(authFacade).login(any());
    }

    @Test
    @DisplayName("POST /api/auth/login — должен вернуть 400 при невалидном теле запроса")
    void login_shouldReturn400_whenInvalidRequest() throws Exception {
        // given
        String requestBody = objectMapper.writeValueAsString(new LoginRequest("not-an-email", ""));

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    // ===== POST /api/auth/register =====

    @Test
    @DisplayName("POST /api/auth/register — должен вернуть 201 и токен при валидных данных")
    void register_shouldReturn201AndToken_whenValidRequest() throws Exception {
        // given
        String requestBody = objectMapper.writeValueAsString(new RegisterRequest(TEST_EMAIL, TEST_PASSWORD));
        when(authFacade.register(any())).thenReturn(new AuthResponse(TEST_TOKEN));

        // when & then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.token").value(TEST_TOKEN));

        verify(authFacade).register(any());
    }

    @Test
    @DisplayName("POST /api/auth/register — должен вернуть 400 при невалидном теле запроса")
    void register_shouldReturn400_whenInvalidRequest() throws Exception {
        // given
        String requestBody = objectMapper.writeValueAsString(new RegisterRequest("", ""));

        // when & then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/login — должен вернуть 401 при неверном пароле")
    void login_shouldReturn401_whenBadCredentials() throws Exception {
        // given
        String requestBody = objectMapper.writeValueAsString(new LoginRequest(TEST_EMAIL, TEST_PASSWORD));
        when(authFacade.login(any())).thenThrow(new BadCredentialsException("Invalid credentials"));

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    @Test
    @DisplayName("POST /api/auth/register — должен вернуть 409 если email уже занят")
    void register_shouldReturn409_whenEmailAlreadyExists() throws Exception {
        // given
        String requestBody = objectMapper.writeValueAsString(new RegisterRequest(TEST_EMAIL, TEST_PASSWORD));
        when(authFacade.register(any())).thenThrow(AlreadyExistsException.userByEmail(TEST_EMAIL));

        // when & then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }
}