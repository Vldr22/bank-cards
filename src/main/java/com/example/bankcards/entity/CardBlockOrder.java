package com.example.bankcards.entity;

import com.example.bankcards.enums.BlockOrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "card_block_orders", indexes = {
        @Index(name = "idx_card_block_orders_card_status", columnList = "card_id, status")
})
public class CardBlockOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "block_request_seq")
    @SequenceGenerator(name = "block_request_seq", sequenceName = "block_request_sequence", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BlockOrderStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public CardBlockOrder(Card card, User user) {
        this.card = card;
        this.user = user;
        this.status = BlockOrderStatus.PENDING;
    }
}
