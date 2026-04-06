package com.example.bankcards.service.facade;

import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.CardStatus;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CardFacade — операции пользователя с картами")
class CardFacadeTest {

    private static final Faker FAKER = new Faker();
    private static final String MASKED_NUMBER = "**** **** **** 1234";

    @Mock
    private CardService cardService;
    @Mock
    private CardEncryptionUtil cardEncryptionUtil;
    @Mock
    private UserService userService;
    @Mock
    private CardBlockOrderService cardBlockOrderService;

    @InjectMocks
    private CardFacade cardFacade;

    private Long userId;
    private Long cardId;
    private User currentUser;

    @BeforeEach
    void setUp() {
        userId = FAKER.number().randomNumber();
        cardId = FAKER.number().randomNumber();

        currentUser = mock(User.class);
        when(currentUser.getId()).thenReturn(userId);
    }

    // ===== getAllMyCards =====

    @Test
    @DisplayName("getAllMyCards — должен вернуть страницу карт текущего пользователя")
    void getAllMyCards_shouldReturnMappedCardPage() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        Card card = mock(Card.class);
        when(card.getCardNumberEncrypted()).thenReturn("encrypted");
        when(card.getCardHolder()).thenReturn(FAKER.name().fullName());
        when(card.getExpiresAt()).thenReturn(LocalDate.now().plusYears(3));
        when(card.getStatus()).thenReturn(CardStatus.ACTIVE);
        when(card.getBalance()).thenReturn(BigDecimal.valueOf(1000));

        Page<Card> cardPage = new PageImpl<>(List.of(card));

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(cardService.getCardsByUserId(userId, pageable)).thenReturn(cardPage);
        when(cardEncryptionUtil.mask("encrypted")).thenReturn(MASKED_NUMBER);

        // when
        Page<CardResponse> result = cardFacade.getAllMyCards(pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).maskedNumber()).isEqualTo(MASKED_NUMBER);
        verify(userService).getCurrentUser();
        verify(cardService).getCardsByUserId(userId, pageable);
        verify(cardEncryptionUtil).mask("encrypted");
    }

    // ===== transfer =====

    @Test
    @DisplayName("transfer — должен вызвать cardService.transfer с данными текущего пользователя")
    void transfer_shouldDelegateToCardService() {
        // given
        Long fromCardId = FAKER.number().randomNumber();
        Long toCardId = FAKER.number().randomNumber();
        BigDecimal amount = BigDecimal.valueOf(500);
        TransferRequest request = new TransferRequest(fromCardId, toCardId, amount);

        when(currentUser.getEmail()).thenReturn(FAKER.internet().emailAddress()); // только здесь нужен
        when(userService.getCurrentUser()).thenReturn(currentUser);

        // when
        cardFacade.transfer(request);

        // then
        verify(userService).getCurrentUser();
        verify(cardService).transfer(fromCardId, toCardId, amount, userId);
    }

    // ===== requestBlock =====

    @Test
    @DisplayName("requestBlock — должен создать заявку на блокировку активной карты")
    void requestBlock_shouldCreateBlockOrder_whenCardIsActive() {
        // given
        Card card = mock(Card.class);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(cardService.getCardByIdAndUserId(cardId, userId)).thenReturn(card);

        // when
        cardFacade.requestBlock(cardId);

        // then
        verify(userService).getCurrentUser();
        verify(cardService).getCardByIdAndUserId(cardId, userId);
        verify(cardService).validateActiveStatus(card);
        verify(cardBlockOrderService).createOrder(card, currentUser);
    }
}