package com.example.bankcards.service.facade;

import com.example.bankcards.constants.ErrorMessages;
import com.example.bankcards.dto.request.CreateCardRequest;
import com.example.bankcards.dto.request.UpdateCardStatusRequest;
import com.example.bankcards.dto.response.AdminCardResponse;
import com.example.bankcards.dto.response.CardBlockOrderResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockOrder;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.enums.UserRole;
import com.example.bankcards.service.CardBlockOrderService;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.CardEncryptionUtil;
import com.example.bankcards.util.CardNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminFacade {

    private final CardService cardService;
    private final UserService userService;
    private final CardBlockOrderService cardBlockOrderService;
    private final CardEncryptionUtil cardEncryptionUtil;

    public Page<AdminCardResponse> getAllCards(Pageable pageable) {
        return cardService.getAllCards(pageable)
                .map(this::toAdminCardResponse);
    }

    public AdminCardResponse createCard(CreateCardRequest request) {
        User user = userService.getUserById(request.userId());

        if (!UserRole.ROLE_USER.equals(user.getRole().getName())) {
            throw new IllegalArgumentException(ErrorMessages.CARD_FOR_ADMIN_NOT_ALLOWED);
        }

        String cardNumberEncrypted = generateAndEncryptCard();
        LocalDate expiresAt = LocalDate.now().plusYears(5);

        Card card = cardService.createCard(user, cardNumberEncrypted, expiresAt, request);

        log.info("Admin created card: userId: {}, cardId: {}", user.getId(), card.getId());
        return toAdminCardResponse(card);
    }

    public AdminCardResponse updateCardStatus(Long id, UpdateCardStatusRequest request) {
        cardService.updateStatus(id, request.status());
        Card card = cardService.getCardById(id);
        log.info("Admin updated card status: cardId: {}, CardStatus: {}", id, request.status());
        return toAdminCardResponse(card);
    }

    public void deleteCard(Long id) {
        cardService.deleteCard(id);
        log.info("Admin deleted card: cardId: {}", id);
    }


    // ===== BLOCK ORDERS =====
    public Page<CardBlockOrderResponse> getBlockOrders(Pageable pageable) {
        return cardBlockOrderService.getPendingOrders(pageable)
                .map(this::toBlockOrderResponse);
    }

    public void approveBlockOrder(Long cardId) {
        cardBlockOrderService.approveOrder(cardId);
        cardService.updateStatus(cardId, CardStatus.BLOCKED);
        log.info("Admin approved block order: cardId: {}", cardId);
    }

    public void rejectBlockOrder(Long cardId) {
        cardBlockOrderService.rejectOrder(cardId);
        log.info("Admin rejected block order: cardId: {}", cardId);
    }


    // ===== HELPER =====
    private String generateAndEncryptCard() {
        String tempCardNumber = CardNumberGenerator.generate();
        return cardEncryptionUtil.encrypt(tempCardNumber);
    }

    private AdminCardResponse toAdminCardResponse(Card card) {
        return new AdminCardResponse(
                card.getId(),
                cardEncryptionUtil.mask(card.getCardNumberEncrypted()),
                card.getCardHolder(),
                card.getExpiresAt(),
                card.getStatus(),
                card.getBalance()
        );
    }

    private CardBlockOrderResponse toBlockOrderResponse(CardBlockOrder order) {
        return new CardBlockOrderResponse(
                order.getId(),
                order.getCard().getId(),
                cardEncryptionUtil.mask(order.getCard().getCardNumberEncrypted()),
                order.getUser().getId(),
                order.getUser().getEmail(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}
