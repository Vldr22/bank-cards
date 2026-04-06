package com.example.bankcards.service.facade;

import com.example.bankcards.dto.request.CreateCardRequest;
import com.example.bankcards.dto.request.UpdateCardStatusRequest;
import com.example.bankcards.dto.request.UpdateUserStatusRequest;
import com.example.bankcards.dto.response.AdminCardResponse;
import com.example.bankcards.dto.response.CardBlockOrderResponse;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockOrder;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.enums.UserRole;
import com.example.bankcards.enums.UserStatus;
import com.example.bankcards.service.CardBlockOrderService;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.CardEncryptionUtil;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminFacade — административные операции")
class AdminFacadeTest {

    private static final Faker FAKER = new Faker();
    private static final String MASKED_NUMBER = "**** **** **** 1234";
    private static final String ENCRYPTED_NUMBER = "encrypted_card_number";

    @Mock
    private CardService cardService;
    @Mock
    private UserService userService;
    @Mock
    private CardBlockOrderService cardBlockOrderService;
    @Mock
    private CardEncryptionUtil cardEncryptionUtil;

    @InjectMocks
    private AdminFacade adminFacade;

    private Long userId;
    private Long cardId;

    @BeforeEach
    void setUp() {
        userId = FAKER.number().randomNumber();
        cardId = FAKER.number().randomNumber();
    }

    // ===== getAllUsers =====

    @Test
    @DisplayName("getAllUsers — должен вернуть страницу пользователей преобразованных в UserResponse")
    void getAllUsers_shouldReturnMappedUserPage() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        Role role = mock(Role.class);
        when(role.getName()).thenReturn(UserRole.ROLE_USER);

        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        when(user.getEmail()).thenReturn(FAKER.internet().emailAddress());
        when(user.getStatus()).thenReturn(UserStatus.ACTIVE);
        when(user.getRole()).thenReturn(role);
        when(user.getCreatedAt()).thenReturn(LocalDateTime.now());

        when(userService.getAllUsers(pageable)).thenReturn(new PageImpl<>(List.of(user)));

        // when
        Page<UserResponse> result = adminFacade.getAllUsers(pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        verify(userService).getAllUsers(pageable);
    }

    // ===== updateUserStatus =====

    @Test
    @DisplayName("updateUserStatus — должен обновить статус и вернуть UserResponse")
    void updateUserStatus_shouldUpdateAndReturnUserResponse() {
        // given
        UpdateUserStatusRequest request = new UpdateUserStatusRequest(UserStatus.BLOCKED);

        Role role = mock(Role.class);
        when(role.getName()).thenReturn(UserRole.ROLE_USER);

        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        when(user.getEmail()).thenReturn(FAKER.internet().emailAddress());
        when(user.getStatus()).thenReturn(UserStatus.BLOCKED);
        when(user.getRole()).thenReturn(role);
        when(user.getCreatedAt()).thenReturn(LocalDateTime.now());

        when(userService.getUserById(userId)).thenReturn(user);

        // when
        UserResponse result = adminFacade.updateUserStatus(userId, request);

        // then
        assertThat(result).isNotNull();
        verify(userService).updateStatus(userId, UserStatus.BLOCKED);
        verify(userService).getUserById(userId);
    }

    // ===== deleteUser =====

    @Test
    @DisplayName("deleteUser — должен вызвать userService.deleteUser")
    void deleteUser_shouldDelegateToUserService() {
        // when
        adminFacade.deleteUser(userId);

        // then
        verify(userService).deleteUser(userId);
    }

    // ===== getAllCards =====

    @Test
    @DisplayName("getAllCards — должен вернуть страницу карт преобразованных в AdminCardResponse")
    void getAllCards_shouldReturnMappedCardPage() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        Card card = mock(Card.class);
        when(card.getId()).thenReturn(cardId);
        when(card.getCardNumberEncrypted()).thenReturn(ENCRYPTED_NUMBER);
        when(card.getCardHolder()).thenReturn(FAKER.name().fullName());
        when(card.getExpiresAt()).thenReturn(LocalDate.now().plusYears(3));
        when(card.getStatus()).thenReturn(CardStatus.ACTIVE);
        when(card.getBalance()).thenReturn(BigDecimal.valueOf(1000));

        when(cardService.getAllCards(pageable)).thenReturn(new PageImpl<>(List.of(card)));
        when(cardEncryptionUtil.mask(ENCRYPTED_NUMBER)).thenReturn(MASKED_NUMBER);

        // when
        Page<AdminCardResponse> result = adminFacade.getAllCards(pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).maskedNumber()).isEqualTo(MASKED_NUMBER);
        verify(cardService).getAllCards(pageable);
        verify(cardEncryptionUtil).mask(ENCRYPTED_NUMBER);
    }

    // ===== createCard =====

    @Test
    @DisplayName("createCard — должен создать карту для USER и вернуть AdminCardResponse")
    void createCard_shouldCreateCardAndReturnResponse_whenUserRole() {
        // given
        Role role = mock(Role.class);
        when(role.getName()).thenReturn(UserRole.ROLE_USER);

        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        when(user.getRole()).thenReturn(role);

        Card card = mock(Card.class);
        when(card.getId()).thenReturn(cardId);
        when(card.getCardNumberEncrypted()).thenReturn(ENCRYPTED_NUMBER);
        when(card.getCardHolder()).thenReturn(FAKER.name().fullName());
        when(card.getExpiresAt()).thenReturn(LocalDate.now().plusYears(5));
        when(card.getStatus()).thenReturn(CardStatus.ACTIVE);
        when(card.getBalance()).thenReturn(BigDecimal.valueOf(500));

        CreateCardRequest request = new CreateCardRequest(userId, FAKER.name().fullName(), BigDecimal.valueOf(500));

        when(userService.getUserById(userId)).thenReturn(user);
        when(cardEncryptionUtil.encrypt(any())).thenReturn(ENCRYPTED_NUMBER);
        when(cardService.createCard(eq(user), eq(ENCRYPTED_NUMBER), any(LocalDate.class), eq(request))).thenReturn(card);
        when(cardEncryptionUtil.mask(ENCRYPTED_NUMBER)).thenReturn(MASKED_NUMBER);

        // when
        AdminCardResponse result = adminFacade.createCard(request);

        // then
        assertThat(result.maskedNumber()).isEqualTo(MASKED_NUMBER);
        verify(userService).getUserById(userId);
        verify(cardEncryptionUtil).encrypt(any());
        verify(cardService).createCard(eq(user), eq(ENCRYPTED_NUMBER), any(LocalDate.class), eq(request));
    }

    @Test
    @DisplayName("createCard — должен выбросить IllegalArgumentException если пользователь ADMIN")
    void createCard_shouldThrowIllegalArgumentException_whenUserIsAdmin() {
        // given
        Role role = mock(Role.class);
        when(role.getName()).thenReturn(UserRole.ROLE_ADMIN);

        User user = mock(User.class);
        when(user.getRole()).thenReturn(role);

        CreateCardRequest request = new CreateCardRequest(userId, FAKER.name().fullName(), BigDecimal.valueOf(500));
        when(userService.getUserById(userId)).thenReturn(user);

        // when & then
        assertThatThrownBy(() -> adminFacade.createCard(request))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(cardService);
        verifyNoInteractions(cardEncryptionUtil);
    }

    // ===== updateCardStatus =====

    @Test
    @DisplayName("updateCardStatus — должен обновить статус карты и вернуть AdminCardResponse")
    void updateCardStatus_shouldUpdateAndReturnResponse() {
        // given
        UpdateCardStatusRequest request = new UpdateCardStatusRequest(CardStatus.BLOCKED);

        Card card = mock(Card.class);
        when(card.getId()).thenReturn(cardId);
        when(card.getCardNumberEncrypted()).thenReturn(ENCRYPTED_NUMBER);
        when(card.getCardHolder()).thenReturn(FAKER.name().fullName());
        when(card.getExpiresAt()).thenReturn(LocalDate.now().plusYears(3));
        when(card.getStatus()).thenReturn(CardStatus.BLOCKED);
        when(card.getBalance()).thenReturn(BigDecimal.valueOf(1000));

        when(cardService.getCardById(cardId)).thenReturn(card);
        when(cardEncryptionUtil.mask(ENCRYPTED_NUMBER)).thenReturn(MASKED_NUMBER);

        // when
        AdminCardResponse result = adminFacade.updateCardStatus(cardId, request);

        // then
        assertThat(result.maskedNumber()).isEqualTo(MASKED_NUMBER);
        verify(cardService).updateStatus(cardId, CardStatus.BLOCKED);
        verify(cardService).getCardById(cardId);
    }

    // ===== deleteCard =====

    @Test
    @DisplayName("deleteCard — должен вызвать cardService.deleteCard")
    void deleteCard_shouldDelegateToCardService() {
        // when
        adminFacade.deleteCard(cardId);

        // then
        verify(cardService).deleteCard(cardId);
    }

    // ===== getBlockOrders =====

    @Test
    @DisplayName("getBlockOrders — должен вернуть страницу PENDING заявок преобразованных в CardBlockOrderResponse")
    void getBlockOrders_shouldReturnMappedBlockOrderPage() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        Card card = mock(Card.class);
        when(card.getId()).thenReturn(cardId);
        when(card.getCardNumberEncrypted()).thenReturn(ENCRYPTED_NUMBER);

        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        when(user.getEmail()).thenReturn(FAKER.internet().emailAddress());

        CardBlockOrder order = mock(CardBlockOrder.class);
        when(order.getId()).thenReturn(FAKER.number().randomNumber());
        when(order.getCard()).thenReturn(card);
        when(order.getUser()).thenReturn(user);
        when(order.getStatus()).thenReturn(com.example.bankcards.enums.BlockOrderStatus.PENDING);
        when(order.getCreatedAt()).thenReturn(LocalDateTime.now());

        when(cardBlockOrderService.getPendingOrders(pageable)).thenReturn(new PageImpl<>(List.of(order)));
        when(cardEncryptionUtil.mask(ENCRYPTED_NUMBER)).thenReturn(MASKED_NUMBER);

        // when
        Page<CardBlockOrderResponse> result = adminFacade.getBlockOrders(pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        verify(cardBlockOrderService).getPendingOrders(pageable);
        verify(cardEncryptionUtil).mask(ENCRYPTED_NUMBER);
    }

    // ===== approveBlockOrder =====

    @Test
    @DisplayName("approveBlockOrder — должен подтвердить заявку и заблокировать карту")
    void approveBlockOrder_shouldApproveOrderAndBlockCard() {
        // when
        adminFacade.approveBlockOrder(cardId);

        // then
        verify(cardBlockOrderService).approveOrder(cardId);
        verify(cardService).updateStatus(cardId, CardStatus.BLOCKED);
    }

    // ===== rejectBlockOrder =====

    @Test
    @DisplayName("rejectBlockOrder — должен отклонить заявку на блокировку")
    void rejectBlockOrder_shouldRejectOrder() {
        // when
        adminFacade.rejectBlockOrder(cardId);

        // then
        verify(cardBlockOrderService).rejectOrder(cardId);
        verifyNoInteractions(cardService);
    }
}