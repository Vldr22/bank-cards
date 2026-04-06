package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockOrder;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.BlockOrderStatus;
import com.example.bankcards.exception.AlreadyExistsException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardBlockOrderRepository;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CardBlockOrderService — управление заявками на блокировку карт")
class CardBlockOrderServiceTest {

    private static final Faker FAKER = new Faker();

    @Mock
    private CardBlockOrderRepository blockOrderRepository;

    @InjectMocks
    private CardBlockOrderService cardBlockOrderService;

    private Long cardId;

    @BeforeEach
    void setUp() {
        cardId = FAKER.number().randomNumber();
    }

    // ===== createOrder =====

    @Test
    @DisplayName("createOrder — должен создать заявку на блокировку")
    void createOrder_shouldSaveBlockOrder_whenNoPendingExists() {
        // given
        Card card = mock(Card.class);
        User user = mock(User.class);
        when(card.getId()).thenReturn(cardId);
        when(blockOrderRepository.existsByCardIdAndStatus(cardId, BlockOrderStatus.PENDING))
                .thenReturn(false);

        // when
        cardBlockOrderService.createOrder(card, user);

        // then
        verify(blockOrderRepository).existsByCardIdAndStatus(cardId, BlockOrderStatus.PENDING);
        verify(blockOrderRepository).save(any(CardBlockOrder.class));
    }

    @Test
    @DisplayName("createOrder — должен выбросить AlreadyExistsException если заявка PENDING уже существует")
    void createOrder_shouldThrowAlreadyExistsException_whenPendingOrderExists() {
        // given
        Card card = mock(Card.class);
        User user = mock(User.class);
        when(card.getId()).thenReturn(cardId);
        when(blockOrderRepository.existsByCardIdAndStatus(cardId, BlockOrderStatus.PENDING))
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> cardBlockOrderService.createOrder(card, user))
                .isInstanceOf(AlreadyExistsException.class);

        verify(blockOrderRepository).existsByCardIdAndStatus(cardId, BlockOrderStatus.PENDING);
        verify(blockOrderRepository, never()).save(any());
    }

    // ===== getPendingOrders =====

    @Test
    @DisplayName("getPendingOrders — должен вернуть страницу заявок со статусом PENDING")
    void getPendingOrders_shouldReturnPageOfPendingOrders() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<CardBlockOrder> expectedPage = new PageImpl<>(List.of(mock(CardBlockOrder.class)));
        when(blockOrderRepository.findByStatus(BlockOrderStatus.PENDING, pageable))
                .thenReturn(expectedPage);

        // when
        Page<CardBlockOrder> result = cardBlockOrderService.getPendingOrders(pageable);

        // then
        assertThat(result).isEqualTo(expectedPage);
        assertThat(result.getContent()).hasSize(1);
        verify(blockOrderRepository).findByStatus(BlockOrderStatus.PENDING, pageable);
    }

    // ===== approveOrder =====

    @Test
    @DisplayName("approveOrder — должен установить статус APPROVED")
    void approveOrder_shouldSetStatusApproved_whenPendingOrderExists() {
        // given
        CardBlockOrder order = mock(CardBlockOrder.class);
        when(blockOrderRepository.findByCardIdAndStatus(cardId, BlockOrderStatus.PENDING))
                .thenReturn(Optional.of(order));

        // when
        cardBlockOrderService.approveOrder(cardId);

        // then
        verify(blockOrderRepository).findByCardIdAndStatus(cardId, BlockOrderStatus.PENDING);
        verify(order).setStatus(BlockOrderStatus.APPROVED);
    }

    @Test
    @DisplayName("approveOrder — должен выбросить NotFoundException если PENDING заявки не существует")
    void approveOrder_shouldThrowNotFoundException_whenNoPendingOrder() {
        // given
        when(blockOrderRepository.findByCardIdAndStatus(cardId, BlockOrderStatus.PENDING))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> cardBlockOrderService.approveOrder(cardId))
                .isInstanceOf(NotFoundException.class);

        verify(blockOrderRepository).findByCardIdAndStatus(cardId, BlockOrderStatus.PENDING);
    }

    // ===== rejectOrder =====

    @Test
    @DisplayName("rejectOrder — должен установить статус REJECTED")
    void rejectOrder_shouldSetStatusRejected_whenPendingOrderExists() {
        // given
        CardBlockOrder order = mock(CardBlockOrder.class);
        when(blockOrderRepository.findByCardIdAndStatus(cardId, BlockOrderStatus.PENDING))
                .thenReturn(Optional.of(order));

        // when
        cardBlockOrderService.rejectOrder(cardId);

        // then
        verify(blockOrderRepository).findByCardIdAndStatus(cardId, BlockOrderStatus.PENDING);
        verify(order).setStatus(BlockOrderStatus.REJECTED);
    }

    @Test
    @DisplayName("rejectOrder — должен выбросить NotFoundException если PENDING заявки не существует")
    void rejectOrder_shouldThrowNotFoundException_whenNoPendingOrder() {
        // given
        when(blockOrderRepository.findByCardIdAndStatus(cardId, BlockOrderStatus.PENDING))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> cardBlockOrderService.rejectOrder(cardId))
                .isInstanceOf(NotFoundException.class);

        verify(blockOrderRepository).findByCardIdAndStatus(cardId, BlockOrderStatus.PENDING);
    }
}