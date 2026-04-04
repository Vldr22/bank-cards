package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockOrder;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.BlockOrderStatus;
import com.example.bankcards.exception.AlreadyExistsException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardBlockOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardBlockOrderService {

    private final CardBlockOrderRepository blockOrderRepository;

    @Transactional
    public void createOrder(Card card, User user) {
        if (blockOrderRepository.existsByCardIdAndStatus(card.getId(), BlockOrderStatus.PENDING)) {
            throw AlreadyExistsException.blockOrderByCardId(card.getId());
        }

        CardBlockOrder order = new CardBlockOrder(card, user);
        blockOrderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public Page<CardBlockOrder> getPendingOrders(Pageable pageable) {
        return blockOrderRepository.findByStatus(BlockOrderStatus.PENDING, pageable);
    }

    @Transactional
    public void approveOrder(Long cardId) {
        CardBlockOrder order = getPendingByCardId(cardId);
        order.setStatus(BlockOrderStatus.APPROVED);
    }

    @Transactional
    public void rejectOrder(Long cardId) {
        CardBlockOrder order = getPendingByCardId(cardId);
        order.setStatus(BlockOrderStatus.REJECTED);
    }

    private CardBlockOrder getPendingByCardId(Long cardId) {
        return blockOrderRepository.findByCardIdAndStatus(cardId, BlockOrderStatus.PENDING)
                .orElseThrow(() -> NotFoundException.blockOrderByCardId(cardId));
    }
}
