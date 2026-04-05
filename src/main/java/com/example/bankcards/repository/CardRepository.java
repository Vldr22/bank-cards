package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.enums.CardStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    @NonNull
    @Query(value = "SELECT c FROM Card c JOIN FETCH c.user WHERE c.user.id = :userId",
            countQuery = "SELECT COUNT(c) FROM Card c WHERE c.user.id = :userId")
    Page<Card> findByUserId(@Param("userId") Long userId, @NonNull Pageable pageable);

    @NonNull
    @Query("SELECT c FROM Card c JOIN FETCH c.user WHERE c.id = :id AND c.user.id = :userId")
    Optional<Card> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @NonNull
    @Query(value = "SELECT c FROM Card c JOIN FETCH c.user",
            countQuery = "SELECT count(c) from Card c")
    Page<Card> findAll(@NonNull Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Card c WHERE c.id = :id AND c.user.id = :userId")
    Optional<Card> findByIdAndUserIdWithLock(@Param("id") Long id, @Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Card c SET c.status = :status WHERE c.expiresAt < :today AND c.status = 'ACTIVE'")
    int updateExpiredCards(@Param("today") LocalDate today, @Param("status") CardStatus status);
}
