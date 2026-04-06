package com.example.bankcards.controller;

import com.example.bankcards.config.TestSecurityConfig;
import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.exception.CardNotActiveException;
import com.example.bankcards.exception.InsufficientBalanceException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.service.facade.CardFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardController.class)
@Import(TestSecurityConfig.class)
@DisplayName("CardController — endpoints карт пользователя")
class CardControllerTest {

    private static final String TEST_MASKED_NUMBER = "**** **** **** 1234";
    private static final String TEST_CARD_HOLDER = "IVAN IVANOV";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CardFacade cardFacade;

    private CardResponse cardResponse;

    @BeforeEach
    void setUp() {
        cardResponse = new CardResponse(
                TEST_MASKED_NUMBER,
                TEST_CARD_HOLDER,
                LocalDate.now().plusYears(3),
                CardStatus.ACTIVE,
                BigDecimal.valueOf(1000)
        );
    }

    // ===== GET /api/cards =====

    @Test
    @DisplayName("GET /api/cards — должен вернуть 200 и страницу карт")
    void getAllCards_shouldReturn200AndCardPage() throws Exception {
        // given
        when(cardFacade.getAllMyCards(any())).thenReturn(
                new PageImpl<>(List.of(cardResponse), PageRequest.of(0, 10), 1)
        );

        // when & then
        mockMvc.perform(get("/api/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].maskedNumber").value(TEST_MASKED_NUMBER))
                .andExpect(jsonPath("$.content[0].cardHolder").value(TEST_CARD_HOLDER));

        verify(cardFacade).getAllMyCards(any());
    }

    // ===== POST /api/cards/transfer =====

    @Test
    @DisplayName("POST /api/cards/transfer — должен вернуть 200 при валидном запросе")
    void transfer_shouldReturn200_whenValidRequest() throws Exception {
        // given
        String requestBody = objectMapper.writeValueAsString(new TransferRequest(1L, 2L, BigDecimal.valueOf(500)));

        // when & then
        mockMvc.perform(post("/api/cards/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(cardFacade).transfer(any());
    }

    @Test
    @DisplayName("POST /api/cards/transfer — должен вернуть 400 при невалидном теле запроса")
    void transfer_shouldReturn400_whenInvalidRequest() throws Exception {
        // given — amount отрицательный
        String requestBody = objectMapper.writeValueAsString(
                new TransferRequest(1L, 2L, BigDecimal.valueOf(-100))
        );

        // when & then
        mockMvc.perform(post("/api/cards/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/cards/transfer — должен вернуть 404 если карта не найдена")
    void transfer_shouldReturn404_whenCardNotFound() throws Exception {
        // given
        String requestBody = objectMapper.writeValueAsString(new TransferRequest(1L, 2L, BigDecimal.valueOf(500)));
        doThrow(NotFoundException.cardById(1L)).when(cardFacade).transfer(any());

        // when & then
        mockMvc.perform(post("/api/cards/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    @Test
    @DisplayName("POST /api/cards/transfer — должен вернуть 422 при недостатке средств")
    void transfer_shouldReturn422_whenInsufficientBalance() throws Exception {
        // given
        String requestBody = objectMapper.writeValueAsString(new TransferRequest(1L, 2L, BigDecimal.valueOf(500)));
        doThrow(InsufficientBalanceException.byCardId(1L)).when(cardFacade).transfer(any());

        // when & then
        mockMvc.perform(post("/api/cards/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    // ===== POST /api/cards/{id}/block-request =====

    @Test
    @DisplayName("POST /api/cards/{id}/block-request — должен вернуть 200 и создать заявку")
    void blockCard_shouldReturn200_whenValidId() throws Exception {
        // when & then
        mockMvc.perform(post("/api/cards/1/block-request"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(cardFacade).requestBlock(1L);
    }

    @Test
    @DisplayName("POST /api/cards/{id}/block-request — должен вернуть 409 если карта не активна")
    void blockCard_shouldReturn409_whenCardNotActive() throws Exception {
        // given
        doThrow(CardNotActiveException.cardById(1L)).when(cardFacade).requestBlock(1L);

        // when & then
        mockMvc.perform(post("/api/cards/1/block-request"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }
}