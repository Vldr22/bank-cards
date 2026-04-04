package com.example.bankcards.service;

import com.example.bankcards.constants.ErrorMessages;
import com.example.bankcards.dto.request.CreateCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.exception.CardNotActiveException;
import com.example.bankcards.exception.InsufficientBalanceException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

    public Card getCardById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> NotFoundException.cardById(id));
    }

    @Transactional(readOnly = true)
    public Page<Card> getAllCards(Pageable pageable) {
        return cardRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Card> getCardsByUserId(Long userId, Pageable pageable) {
        return cardRepository.findByUserId(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Card getCardByIdAndUserId(Long id, Long userId) {
        return cardRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> NotFoundException.cardById(id));
    }

    @Transactional
    public Card createCard(User user, String cardNumber, LocalDate expiresAt, CreateCardRequest request) {
        Card card = new Card(
                request.initialBalance(),
                CardStatus.ACTIVE,
                expiresAt,
                request.cardHolder(),
                cardNumber,
                user
        );

        return cardRepository.save(card);
    }

    @Transactional
    public void updateStatus(Long id, CardStatus status) {
        Card card = getCardById(id);
        card.setStatus(status);
    }

    @Transactional
    public void deleteCard(Long id) {
        Card card = getCardById(id);
        cardRepository.delete(card);
    }

    public void validateActiveStatus(Card card) {
        if (!card.getStatus().equals(CardStatus.ACTIVE)) {
            throw CardNotActiveException.cardById(card.getId());
        }
    }

    @Transactional
    public void transfer(Long fromCardId, Long toCardId, BigDecimal amount, Long userId) {
        if (fromCardId.equals(toCardId)) {
            throw new IllegalArgumentException(ErrorMessages.TRANSFER_SAME_CARD);
        }

        Card fromCard = cardRepository.findByIdAndUserIdWithLock(fromCardId, userId)
                .orElseThrow(() -> NotFoundException.cardById(fromCardId));
        Card toCard = cardRepository.findByIdAndUserIdWithLock(toCardId, userId)
                .orElseThrow(() -> NotFoundException.cardById(toCardId));

        validateActiveStatus(fromCard);
        validateActiveStatus(toCard);

        if (fromCard.getBalance().compareTo(amount) < 0) {
            throw InsufficientBalanceException.byCardId(fromCardId);
        }

        fromCard.setBalance(fromCard.getBalance().subtract(amount));
        toCard.setBalance(toCard.getBalance().add(amount));

    }

}
