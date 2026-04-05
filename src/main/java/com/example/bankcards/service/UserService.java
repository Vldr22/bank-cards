package com.example.bankcards.service;

import com.example.bankcards.constants.SecurityErrorMessages;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.UserStatus;
import com.example.bankcards.exception.AlreadyExistsException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.UserBlockedException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getCurrentUser() {
        Optional<String> username = SecurityUtils.getCurrentUsername();

        if (username.isEmpty()) {
            log.error("Authentication missing in spring security context");
            throw new IllegalStateException(SecurityErrorMessages.MISSING_AUTHENTICATION_CONTEXT);
        }

        return getUserByEmail(username.get());
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> NotFoundException.userByEmail(email));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> NotFoundException.userById(id));
    }

    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public User createUser(String email, String password, Role role) {
        if (userRepository.existsByEmail(email)) {
            throw AlreadyExistsException.userByEmail(email);
        }

        User user = new User(
                email,
                passwordEncoder.encode(password),
                UserStatus.ACTIVE,
                role
        );

        return userRepository.save(user);
    }

    @Transactional
    public void updateStatus(Long id, UserStatus status) {
        User user = getUserById(id);
        user.setStatus(status);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        user.setStatus(UserStatus.DELETED);
    }

    public void validateUserStatus(User user) {
        switch (user.getStatus()) {
            case BLOCKED -> throw UserBlockedException.blockedByEmail(user.getEmail());
            case DELETED -> throw UserBlockedException.deletedByMail(user.getEmail());
            case ACTIVE -> {}
        }
    }


}
