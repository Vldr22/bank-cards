package com.example.bankcards.repository;

import com.example.bankcards.entity.CardBlockOrder;
import com.example.bankcards.enums.BlockOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardBlockOrderRepository extends JpaRepository<CardBlockOrder, Long> {

    @NonNull
    @Query("SELECT o FROM CardBlockOrder o JOIN FETCH o.card JOIN FETCH o.user WHERE o.status = :status")
    Page<CardBlockOrder> findByStatus(@Param("status") BlockOrderStatus status, @NonNull Pageable pageable);

    @Query("SELECT o FROM CardBlockOrder o JOIN FETCH o.card JOIN FETCH o.user " +
            "WHERE o.card.id = :cardId AND o.status = :status")
    Optional<CardBlockOrder> findByCardIdAndStatus(
            @Param("cardId") Long cardId,
            @Param("status") BlockOrderStatus status);

    boolean existsByCardIdAndStatus(Long cardId, BlockOrderStatus status);

}
