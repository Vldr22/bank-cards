package com.example.bankcards.service.facade;

import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.CardBlockOrderService;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.CardEncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardFacade {

    private final CardService cardService;
    private final CardEncryptionUtil cardEncryptionUtil;
    private final UserService userService;
    private final CardBlockOrderService cardBlockOrderService;

    public Page<CardResponse> getAllMyCards(Pageable pageable) {
        User user = userService.getCurrentUser();
        return cardService.getCardsByUserId(user.getId(), pageable)
                .map(this::toCardResponse);
    }

    public void transfer(TransferRequest request) {
        User user = userService.getCurrentUser();
        cardService.transfer(
                request.fromCardId(),
                request.toCardId(),
                request.amount(),
                user.getId()
        );
        log.info("Transfer completed: userEmail: {}, fromCardId: {}, toCardId: {}, amount: {}",
                user.getEmail(), request.fromCardId(), request.toCardId(), request.amount());
    }

    public void requestBlock(Long id) {
        User user = userService.getCurrentUser();
        Card card = cardService.getCardByIdAndUserId(id, user.getId());
        cardService.validateActiveStatus(card);
        cardBlockOrderService.createOrder(card, user);
        log.info("User requested block: userId: {}, cardId: {}", user.getId(), id);
    }

    private CardResponse toCardResponse(Card card) {
        return new CardResponse(
                cardEncryptionUtil.mask(card.getCardNumberEncrypted()),
                card.getCardHolder(),
                card.getExpiresAt(),
                card.getStatus(),
                card.getBalance()
        );
    }
}
