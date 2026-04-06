package com.example.bankcards.controller;

import com.example.bankcards.config.TestSecurityConfig;
import com.example.bankcards.dto.request.CreateCardRequest;
import com.example.bankcards.dto.request.UpdateCardStatusRequest;
import com.example.bankcards.dto.request.UpdateUserStatusRequest;
import com.example.bankcards.dto.response.AdminCardResponse;
import com.example.bankcards.dto.response.CardBlockOrderResponse;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.enums.BlockOrderStatus;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.enums.UserRole;
import com.example.bankcards.enums.UserStatus;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.service.facade.AdminFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@Import(TestSecurityConfig.class)
@DisplayName("AdminController — endpoints администрирования")
class AdminControllerTest {

    private static final Faker FAKER = new Faker();
    private static final String MASKED_NUMBER = "**** **** **** 1234";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdminFacade adminFacade;

    private Long userId;
    private Long cardId;
    private UserResponse userResponse;
    private AdminCardResponse adminCardResponse;

    @BeforeEach
    void setUp() {
        userId = FAKER.number().randomNumber();
        cardId = FAKER.number().randomNumber();

        userResponse = new UserResponse(
                userId,
                FAKER.internet().emailAddress(),
                UserStatus.ACTIVE,
                UserRole.ROLE_USER,
                LocalDateTime.now()
        );

        adminCardResponse = new AdminCardResponse(
                cardId,
                MASKED_NUMBER,
                FAKER.name().fullName(),
                LocalDate.now().plusYears(3),
                CardStatus.ACTIVE,
                BigDecimal.valueOf(1000)
        );
    }

    // ===== GET /api/admin/users =====

    @Test
    @DisplayName("GET /api/admin/users — должен вернуть 200 и страницу пользователей")
    void getAllUsers_shouldReturn200AndUserPage() throws Exception {
        // given
        when(adminFacade.getAllUsers(any())).thenReturn(
                new PageImpl<>(List.of(userResponse), PageRequest.of(0, 10), 1)
        );

        // when & then
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("ACTIVE"));

        verify(adminFacade).getAllUsers(any());
    }

    // ===== PATCH /api/admin/users/{id}/status =====

    @Test
    @DisplayName("PATCH /api/admin/users/{id}/status — должен вернуть 200 и обновлённого пользователя")
    void updateUserStatus_shouldReturn200AndUpdatedUser() throws Exception {
        // given
        UpdateUserStatusRequest request = new UpdateUserStatusRequest(UserStatus.BLOCKED);
        when(adminFacade.updateUserStatus(eq(userId), any())).thenReturn(userResponse);

        // when & then
        mockMvc.perform(patch("/api/admin/users/{id}/status", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(adminFacade).updateUserStatus(eq(userId), any());
    }

    @Test
    @DisplayName("PATCH /api/admin/users/{id}/status — должен вернуть 404 если пользователь не найден")
    void updateUserStatus_shouldReturn404_whenUserNotFound() throws Exception {
        // given
        UpdateUserStatusRequest request = new UpdateUserStatusRequest(UserStatus.BLOCKED);
        when(adminFacade.updateUserStatus(eq(userId), any())).thenThrow(NotFoundException.userById(userId));

        // when & then
        mockMvc.perform(patch("/api/admin/users/{id}/status", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    // ===== DELETE /api/admin/users/{id} =====

    @Test
    @DisplayName("DELETE /api/admin/users/{id} — должен вернуть 204")
    void deleteUser_shouldReturn204() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/admin/users/{id}", userId))
                .andExpect(status().isNoContent());

        verify(adminFacade).deleteUser(userId);
    }

    // ===== GET /api/admin/cards =====

    @Test
    @DisplayName("GET /api/admin/cards — должен вернуть 200 и страницу карт")
    void getAllCards_shouldReturn200AndCardPage() throws Exception {
        // given
        when(adminFacade.getAllCards(any())).thenReturn(
                new PageImpl<>(List.of(adminCardResponse), PageRequest.of(0, 10), 1)
        );

        // when & then
        mockMvc.perform(get("/api/admin/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].maskedNumber").value(MASKED_NUMBER));

        verify(adminFacade).getAllCards(any());
    }

    // ===== POST /api/admin/cards =====

    @Test
    @DisplayName("POST /api/admin/cards — должен вернуть 201 и созданную карту")
    void createCard_shouldReturn201AndCreatedCard() throws Exception {
        // given
        CreateCardRequest request = new CreateCardRequest(userId, FAKER.name().fullName(), BigDecimal.valueOf(500));
        when(adminFacade.createCard(any())).thenReturn(adminCardResponse);

        // when & then
        mockMvc.perform(post("/api/admin/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.maskedNumber").value(MASKED_NUMBER));

        verify(adminFacade).createCard(any());
    }

    @Test
    @DisplayName("POST /api/admin/cards — должен вернуть 400 если карта создаётся для админа")
    void createCard_shouldReturn400_whenUserIsAdmin() throws Exception {
        // given
        CreateCardRequest request = new CreateCardRequest(userId, FAKER.name().fullName(), BigDecimal.valueOf(500));
        when(adminFacade.createCard(any())).thenThrow(new IllegalArgumentException("Card for admin not allowed"));

        // when & then
        mockMvc.perform(post("/api/admin/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    @Test
    @DisplayName("POST /api/admin/cards — должен вернуть 404 если пользователь не найден")
    void createCard_shouldReturn404_whenUserNotFound() throws Exception {
        // given
        CreateCardRequest request = new CreateCardRequest(userId, FAKER.name().fullName(), BigDecimal.valueOf(500));
        when(adminFacade.createCard(any())).thenThrow(NotFoundException.userById(userId));

        // when & then
        mockMvc.perform(post("/api/admin/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    // ===== PATCH /api/admin/cards/{cardId}/status =====

    @Test
    @DisplayName("PATCH /api/admin/cards/{cardId}/status — должен вернуть 200 и обновлённую карту")
    void updateCardStatus_shouldReturn200AndUpdatedCard() throws Exception {
        // given
        UpdateCardStatusRequest request = new UpdateCardStatusRequest(CardStatus.BLOCKED);
        when(adminFacade.updateCardStatus(eq(cardId), any())).thenReturn(adminCardResponse);

        // when & then
        mockMvc.perform(patch("/api/admin/cards/{cardId}/status", cardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(adminFacade).updateCardStatus(eq(cardId), any());
    }

    // ===== DELETE /api/admin/cards/{cardId} =====

    @Test
    @DisplayName("DELETE /api/admin/cards/{cardId} — должен вернуть 204")
    void deleteCard_shouldReturn204() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/admin/cards/{cardId}", cardId))
                .andExpect(status().isNoContent());

        verify(adminFacade).deleteCard(cardId);
    }

    // ===== GET /api/admin/block-orders =====

    @Test
    @DisplayName("GET /api/admin/block-orders — должен вернуть 200 и страницу заявок")
    void getAllBlockOrders_shouldReturn200AndBlockOrderPage() throws Exception {
        // given
        CardBlockOrderResponse orderResponse = new CardBlockOrderResponse(
                FAKER.number().randomNumber(),
                cardId,
                MASKED_NUMBER,
                userId,
                FAKER.internet().emailAddress(),
                BlockOrderStatus.PENDING,
                LocalDateTime.now()
        );
        when(adminFacade.getBlockOrders(any())).thenReturn(
                new PageImpl<>(List.of(orderResponse), PageRequest.of(0, 10), 1)
        );

        // when & then
        mockMvc.perform(get("/api/admin/block-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("PENDING"));

        verify(adminFacade).getBlockOrders(any());
    }

    // ===== PATCH /api/admin/block-orders/{cardId}/approve =====

    @Test
    @DisplayName("PATCH /api/admin/block-orders/{cardId}/approve — должен вернуть 200")
    void approveBlockOrder_shouldReturn200() throws Exception {
        // when & then
        mockMvc.perform(patch("/api/admin/block-orders/{cardId}/approve", cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(adminFacade).approveBlockOrder(cardId);
    }

    @Test
    @DisplayName("PATCH /api/admin/block-orders/{cardId}/approve — должен вернуть 404 если заявка не найдена")
    void approveBlockOrder_shouldReturn404_whenOrderNotFound() throws Exception {
        // given
        doThrow(NotFoundException.blockOrderByCardId(cardId)).when(adminFacade).approveBlockOrder(cardId);

        // when & then
        mockMvc.perform(patch("/api/admin/block-orders/{cardId}/approve", cardId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    // ===== PATCH /api/admin/block-orders/{cardId}/reject =====

    @Test
    @DisplayName("PATCH /api/admin/block-orders/{cardId}/reject — должен вернуть 200")
    void rejectBlockOrder_shouldReturn200() throws Exception {
        // when & then
        mockMvc.perform(patch("/api/admin/block-orders/{cardId}/reject", cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(adminFacade).rejectBlockOrder(cardId);
    }

    @Test
    @DisplayName("PATCH /api/admin/block-orders/{cardId}/reject — должен вернуть 404 если заявка не найдена")
    void rejectBlockOrder_shouldReturn404_whenOrderNotFound() throws Exception {
        // given
        doThrow(NotFoundException.blockOrderByCardId(cardId)).when(adminFacade).rejectBlockOrder(cardId);

        // when & then
        mockMvc.perform(patch("/api/admin/block-orders/{cardId}/reject", cardId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }
}