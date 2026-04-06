package com.example.bankcards.service;

import com.example.bankcards.dto.request.CreateCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.exception.CardNotActiveException;
import com.example.bankcards.exception.InsufficientBalanceException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CardService — управление банковскими картами")
class CardServiceTest {

    private static final Faker FAKER = new Faker();

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardService cardService;

    private Long cardId;
    private Long userId;

    @BeforeEach
    void setUp() {
        cardId = FAKER.number().randomNumber();
        userId = FAKER.number().randomNumber();
    }

    // ===== getCardById =====

    @Test
    @DisplayName("getCardById — должен вернуть карту если она существует")
    void getCardById_shouldReturnCard_whenExists() {
        // given
        Card card = mock(Card.class);
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        // when
        Card result = cardService.getCardById(cardId);

        // then
        assertThat(result).isEqualTo(card);
        verify(cardRepository).findById(cardId);
    }

    @Test
    @DisplayName("getCardById — должен выбросить NotFoundException если карта не найдена")
    void getCardById_shouldThrowNotFoundException_whenNotFound() {
        // given
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> cardService.getCardById(cardId))
                .isInstanceOf(NotFoundException.class);

        verify(cardRepository).findById(cardId);
    }

    // ===== getCardByIdAndUserId =====

    @Test
    @DisplayName("getCardByIdAndUserId — должен вернуть карту если принадлежит пользователю")
    void getCardByIdAndUserId_shouldReturnCard_whenBelongsToUser() {
        // given
        Card card = mock(Card.class);
        when(cardRepository.findByIdAndUserId(cardId, userId)).thenReturn(Optional.of(card));

        // when
        Card result = cardService.getCardByIdAndUserId(cardId, userId);

        // then
        assertThat(result).isEqualTo(card);
        verify(cardRepository).findByIdAndUserId(cardId, userId);
    }

    @Test
    @DisplayName("getCardByIdAndUserId — должен выбросить NotFoundException если карта не принадлежит пользователю")
    void getCardByIdAndUserId_shouldThrowNotFoundException_whenNotBelongsToUser() {
        // given
        when(cardRepository.findByIdAndUserId(cardId, userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> cardService.getCardByIdAndUserId(cardId, userId))
                .isInstanceOf(NotFoundException.class);

        verify(cardRepository).findByIdAndUserId(cardId, userId);
    }

    // ===== getAllCards =====

    @Test
    @DisplayName("getAllCards — должен вернуть страницу со всеми картами")
    void getAllCards_shouldReturnPageOfCards() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Card> expectedPage = new PageImpl<>(List.of(mock(Card.class)));
        when(cardRepository.findAll(pageable)).thenReturn(expectedPage);

        // when
        Page<Card> result = cardService.getAllCards(pageable);

        // then
        assertThat(result).isEqualTo(expectedPage);
        assertThat(result.getContent()).hasSize(1);
        verify(cardRepository).findAll(pageable);
    }

    // ===== getCardsByUserId =====

    @Test
    @DisplayName("getCardsByUserId — должен вернуть страницу карт конкретного пользователя")
    void getCardsByUserId_shouldReturnPageOfUserCards() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Card> expectedPage = new PageImpl<>(List.of(mock(Card.class)));
        when(cardRepository.findByUserId(userId, pageable)).thenReturn(expectedPage);

        // when
        Page<Card> result = cardService.getCardsByUserId(userId, pageable);

        // then
        assertThat(result).isEqualTo(expectedPage);
        assertThat(result.getContent()).hasSize(1);
        verify(cardRepository).findByUserId(userId, pageable);
    }

    // ===== createCard =====

    @Test
    @DisplayName("createCard — должен создать и вернуть новую карту")
    void createCard_shouldCreateAndReturnCard() {
        // given
        User user = mock(User.class);
        String encryptedNumber = FAKER.finance().creditCard();
        LocalDate expiresAt = LocalDate.now().plusYears(5);
        BigDecimal initialBalance = BigDecimal.valueOf(500);
        String cardHolder = FAKER.name().fullName();

        CreateCardRequest request = new CreateCardRequest(userId, cardHolder, initialBalance);
        Card savedCard = mock(Card.class);
        when(cardRepository.save(any(Card.class))).thenReturn(savedCard);

        // when
        Card result = cardService.createCard(user, encryptedNumber, expiresAt, request);

        // then
        assertThat(result).isEqualTo(savedCard);
        verify(cardRepository).save(any(Card.class));
    }

    // ===== updateStatus =====

    @Test
    @DisplayName("updateStatus — должен обновить статус карты")
    void updateStatus_shouldUpdateCardStatus() {
        // given
        Card card = mock(Card.class);
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        // when
        cardService.updateStatus(cardId, CardStatus.BLOCKED);

        // then
        verify(cardRepository).findById(cardId);
        verify(card).setStatus(CardStatus.BLOCKED);
    }

    // ===== deleteCard =====

    @Test
    @DisplayName("deleteCard — должен удалить карту если она существует")
    void deleteCard_shouldDeleteCard_whenExists() {
        // given
        Card card = mock(Card.class);
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        // when
        cardService.deleteCard(cardId);

        // then
        verify(cardRepository).findById(cardId);
        verify(cardRepository).delete(card);
    }

    @Test
    @DisplayName("deleteCard — должен выбросить NotFoundException если карта не найдена")
    void deleteCard_shouldThrowNotFoundException_whenNotFound() {
        // given
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> cardService.deleteCard(cardId))
                .isInstanceOf(NotFoundException.class);

        verify(cardRepository, never()).delete(any());
    }

    // ===== validateActiveStatus =====

    @Test
    @DisplayName("validateActiveStatus — не должен выбрасывать исключение если карта активна")
    void validateActiveStatus_shouldNotThrow_whenCardIsActive() {
        // given
        Card card = mock(Card.class);
        when(card.getStatus()).thenReturn(CardStatus.ACTIVE);

        // when & then
        assertThatNoException()
                .isThrownBy(() -> cardService.validateActiveStatus(card));
    }

    @Test
    @DisplayName("validateActiveStatus — должен выбросить CardNotActiveException если карта заблокирована")
    void validateActiveStatus_shouldThrowCardNotActiveException_whenCardIsBlocked() {
        // given
        Card card = mock(Card.class);
        when(card.getStatus()).thenReturn(CardStatus.BLOCKED);

        // when & then
        assertThatThrownBy(() -> cardService.validateActiveStatus(card))
                .isInstanceOf(CardNotActiveException.class);
    }

    // ===== transfer =====

    @Test
    @DisplayName("transfer — должен выбросить IllegalArgumentException при переводе на ту же карту")
    void transfer_shouldThrowIllegalArgumentException_whenSameCard() {
        // given
        BigDecimal amount = BigDecimal.valueOf(100);

        // when & then
        assertThatThrownBy(() -> cardService.transfer(cardId, cardId, amount, userId))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(cardRepository);
    }

    @Test
    @DisplayName("transfer — должен выбросить InsufficientBalanceException при нехватке средств")
    void transfer_shouldThrowInsufficientBalanceException_whenNotEnoughBalance() {
        // given
        Long toCardId = FAKER.number().randomNumber();
        BigDecimal amount = BigDecimal.valueOf(1000);

        Card fromCard = mock(Card.class);
        Card toCard = mock(Card.class);
        when(fromCard.getStatus()).thenReturn(CardStatus.ACTIVE);
        when(fromCard.getBalance()).thenReturn(BigDecimal.valueOf(50));
        when(toCard.getStatus()).thenReturn(CardStatus.ACTIVE);

        when(cardRepository.findByIdAndUserIdWithLock(cardId, userId)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdAndUserIdWithLock(toCardId, userId)).thenReturn(Optional.of(toCard));

        // when & then
        assertThatThrownBy(() -> cardService.transfer(cardId, toCardId, amount, userId))
                .isInstanceOf(InsufficientBalanceException.class);
    }

    @Test
    @DisplayName("transfer — должен выбросить CardNotActiveException если карта заблокирована")
    void transfer_shouldThrowCardNotActiveException_whenCardIsBlocked() {
        // given
        Long toCardId = FAKER.number().randomNumber();
        BigDecimal amount = BigDecimal.valueOf(100);

        Card fromCard = mock(Card.class);
        Card toCard = mock(Card.class);
        when(fromCard.getStatus()).thenReturn(CardStatus.BLOCKED);

        when(cardRepository.findByIdAndUserIdWithLock(cardId, userId)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdAndUserIdWithLock(toCardId, userId)).thenReturn(Optional.of(toCard));

        // when & then
        assertThatThrownBy(() -> cardService.transfer(cardId, toCardId, amount, userId))
                .isInstanceOf(CardNotActiveException.class);
    }

    @Test
    @DisplayName("transfer — должен успешно перевести средства между картами")
    void transfer_shouldTransferFunds_whenValid() {
        // given
        Long toCardId = FAKER.number().randomNumber();
        BigDecimal amount = BigDecimal.valueOf(200);
        BigDecimal fromBalance = BigDecimal.valueOf(1000);
        BigDecimal toBalance = BigDecimal.valueOf(300);

        Card fromCard = mock(Card.class);
        Card toCard = mock(Card.class);
        when(fromCard.getStatus()).thenReturn(CardStatus.ACTIVE);
        when(fromCard.getBalance()).thenReturn(fromBalance);
        when(toCard.getStatus()).thenReturn(CardStatus.ACTIVE);
        when(toCard.getBalance()).thenReturn(toBalance);

        when(cardRepository.findByIdAndUserIdWithLock(cardId, userId)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdAndUserIdWithLock(toCardId, userId)).thenReturn(Optional.of(toCard));

        // when
        cardService.transfer(cardId, toCardId, amount, userId);

        // then
        verify(fromCard).setBalance(fromBalance.subtract(amount));
        verify(toCard).setBalance(toBalance.add(amount));
    }

    // ===== expireOutdatedCards =====

    @Test
    @DisplayName("expireOutdatedCards — должен обновить статус просроченных карт и вернуть количество")
    void expireOutdatedCards_shouldReturnCountOfExpiredCards() {
        // given
        int expectedCount = FAKER.number().numberBetween(1, 10);
        when(cardRepository.updateExpiredCards(any(LocalDate.class), eq(CardStatus.EXPIRED)))
                .thenReturn(expectedCount);

        // when
        int result = cardService.expireOutdatedCards();

        // then
        assertThat(result).isEqualTo(expectedCount);
        verify(cardRepository).updateExpiredCards(any(LocalDate.class), eq(CardStatus.EXPIRED));
    }
}