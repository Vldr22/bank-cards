package com.example.bankcards.service;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.UserRole;
import com.example.bankcards.enums.UserStatus;
import com.example.bankcards.exception.AlreadyExistsException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.UserBlockedException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.SecurityUtils;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService — управление пользователями")
class UserServiceTest {

    private static final Faker FAKER = new Faker();
    private static final String TEST_PASSWORD = "TestPassword123!";

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private Long userId;
    private String email;
    private Role role;

    @BeforeEach
    void setUp() {
        userId = FAKER.number().randomNumber();
        email = FAKER.internet().emailAddress();
        role = new Role(1L, UserRole.ROLE_USER);
    }

    // ===== getCurrentUser =====

    @Test
    @DisplayName("getCurrentUser — должен вернуть текущего пользователя из SecurityContext")
    void getCurrentUser_shouldReturnCurrentUser_whenAuthenticated() {
        // given
        User user = mock(User.class);
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUsername).thenReturn(Optional.of(email));
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

            // when
            User result = userService.getCurrentUser();

            // then
            assertThat(result).isEqualTo(user);
            verify(userRepository).findByEmail(email);
        }
    }

    @Test
    @DisplayName("getCurrentUser — должен выбросить IllegalStateException если SecurityContext пуст")
    void getCurrentUser_shouldThrowIllegalStateException_whenContextIsEmpty() {
        // given
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUsername).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.getCurrentUser())
                    .isInstanceOf(IllegalStateException.class);

            verifyNoInteractions(userRepository);
        }
    }

    // ===== getUserByEmail =====

    @Test
    @DisplayName("getUserByEmail — должен вернуть пользователя если он существует")
    void getUserByEmail_shouldReturnUser_whenExists() {
        // given
        User user = mock(User.class);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // when
        User result = userService.getUserByEmail(email);

        // then
        assertThat(result).isEqualTo(user);
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("getUserByEmail — должен выбросить NotFoundException если пользователь не найден")
    void getUserByEmail_shouldThrowNotFoundException_whenNotFound() {
        // given
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUserByEmail(email))
                .isInstanceOf(NotFoundException.class);

        verify(userRepository).findByEmail(email);
    }

    // ===== getUserById =====

    @Test
    @DisplayName("getUserById — должен вернуть пользователя если он существует")
    void getUserById_shouldReturnUser_whenExists() {
        // given
        User user = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        User result = userService.getUserById(userId);

        // then
        assertThat(result).isEqualTo(user);
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("getUserById — должен выбросить NotFoundException если пользователь не найден")
    void getUserById_shouldThrowNotFoundException_whenNotFound() {
        // given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(NotFoundException.class);

        verify(userRepository).findById(userId);
    }

    // ===== getAllUsers =====

    @Test
    @DisplayName("getAllUsers — должен вернуть страницу пользователей")
    void getAllUsers_shouldReturnPageOfUsers() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> expectedPage = new PageImpl<>(List.of(mock(User.class)));
        when(userRepository.findAll(pageable)).thenReturn(expectedPage);

        // when
        Page<User> result = userService.getAllUsers(pageable);

        // then
        assertThat(result).isEqualTo(expectedPage);
        assertThat(result.getContent()).hasSize(1);
        verify(userRepository).findAll(pageable);
    }

    // ===== existsByEmail =====

    @Test
    @DisplayName("existsByEmail — должен вернуть true если email уже зарегистрирован")
    void existsByEmail_shouldReturnTrue_whenEmailExists() {
        // given
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // when
        boolean result = userService.existsByEmail(email);

        // then
        assertThat(result).isTrue();
        verify(userRepository).existsByEmail(email);
    }

    @Test
    @DisplayName("existsByEmail — должен вернуть false если email не зарегистрирован")
    void existsByEmail_shouldReturnFalse_whenEmailNotExists() {
        // given
        when(userRepository.existsByEmail(email)).thenReturn(false);

        // when
        boolean result = userService.existsByEmail(email);

        // then
        assertThat(result).isFalse();
        verify(userRepository).existsByEmail(email);
    }

    // ===== createUser =====

    @Test
    @DisplayName("createUser — должен создать и вернуть нового пользователя")
    void createUser_shouldCreateAndReturnUser_whenEmailNotExists() {
        // given
        String encodedPassword = "encoded_TestPassword123!";
        User savedUser = mock(User.class);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // when
        User result = userService.createUser(email, TEST_PASSWORD, role);

        // then
        assertThat(result).isEqualTo(savedUser);
        verify(userRepository).existsByEmail(email);
        verify(passwordEncoder).encode(TEST_PASSWORD);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("createUser — должен выбросить AlreadyExistsException если email уже занят")
    void createUser_shouldThrowAlreadyExistsException_whenEmailAlreadyExists() {
        // given
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.createUser(email, TEST_PASSWORD, role))
                .isInstanceOf(AlreadyExistsException.class);

        verify(userRepository).existsByEmail(email);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    // ===== updateStatus =====

    @Test
    @DisplayName("updateStatus — должен обновить статус пользователя")
    void updateStatus_shouldUpdateUserStatus() {
        // given
        User user = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        userService.updateStatus(userId, UserStatus.BLOCKED);

        // then
        verify(userRepository).findById(userId);
        verify(user).setStatus(UserStatus.BLOCKED);
    }

    // ===== deleteUser =====

    @Test
    @DisplayName("deleteUser — должен установить статус DELETED (soft delete)")
    void deleteUser_shouldSetStatusDeleted() {
        // given
        User user = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        userService.deleteUser(userId);

        // then
        verify(userRepository).findById(userId);
        verify(user).setStatus(UserStatus.DELETED);
    }

    @Test
    @DisplayName("deleteUser — должен выбросить NotFoundException если пользователь не найден")
    void deleteUser_shouldThrowNotFoundException_whenNotFound() {
        // given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(NotFoundException.class);

        verify(userRepository).findById(userId);
    }

    // ===== validateUserStatus =====

    @Test
    @DisplayName("validateUserStatus — не должен выбрасывать исключение если пользователь активен")
    void validateUserStatus_shouldNotThrow_whenUserIsActive() {
        // given
        User user = mock(User.class);
        when(user.getStatus()).thenReturn(UserStatus.ACTIVE);

        // when & then
        assertThatNoException()
                .isThrownBy(() -> userService.validateUserStatus(user));
    }

    @Test
    @DisplayName("validateUserStatus — должен выбросить UserBlockedException если пользователь заблокирован")
    void validateUserStatus_shouldThrowUserBlockedException_whenBlocked() {
        // given
        User user = mock(User.class);
        when(user.getStatus()).thenReturn(UserStatus.BLOCKED);
        when(user.getEmail()).thenReturn(email);

        // when & then
        assertThatThrownBy(() -> userService.validateUserStatus(user))
                .isInstanceOf(UserBlockedException.class);
    }

    @Test
    @DisplayName("validateUserStatus — должен выбросить UserBlockedException если пользователь удалён")
    void validateUserStatus_shouldThrowUserBlockedException_whenDeleted() {
        // given
        User user = mock(User.class);
        when(user.getStatus()).thenReturn(UserStatus.DELETED);
        when(user.getEmail()).thenReturn(email);

        // when & then
        assertThatThrownBy(() -> userService.validateUserStatus(user))
                .isInstanceOf(UserBlockedException.class);
    }
}
