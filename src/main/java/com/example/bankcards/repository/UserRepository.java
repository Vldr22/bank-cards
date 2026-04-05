package com.example.bankcards.repository;

import com.example.bankcards.entity.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @NotNull
    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.id = :id")
    Optional<User> findById(@Param("id") Long id);

    @NotNull
    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    boolean existsByEmail(String email);

    @NotNull
    @Query("SELECT u FROM User u JOIN FETCH u.role")
    Page<User> findAll(Pageable pageable);

}
