package com.example.bankcards.service.facade;

import com.example.bankcards.dto.request.LoginRequest;
import com.example.bankcards.dto.request.RegisterRequest;
import com.example.bankcards.dto.response.AuthResponse;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.UserRole;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.security.JwtTokenService;
import com.example.bankcards.service.RoleService;
import com.example.bankcards.service.UserService;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthFacade — аутентификация и регистрация")
class AuthFacadeTest {

    private static final Faker FAKER = new Faker();
    private static final String TEST_PASSWORD = "TestPassword123!";
    private static final String TEST_TOKEN = "test.jwt.token";

    @Mock
    private UserService userService;
    @Mock
    private RoleService roleService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenService jwtTokenService;

    @InjectMocks
    private AuthFacade authFacade;

    private String email;

    @BeforeEach
    void setUp() {
        email = FAKER.internet().emailAddress();
    }

    // ===== login =====

    @Test
    @DisplayName("login — должен вернуть токен при верных credentials")
    void login_shouldReturnToken_whenCredentialsAreValid() {
        // given
        LoginRequest request = new LoginRequest(email, TEST_PASSWORD);

        User user = mock(User.class);
        Role role = mock(Role.class);
        when(user.getPassword()).thenReturn("encodedPassword");
        when(user.getEmail()).thenReturn(email);
        when(user.getRole()).thenReturn(role);
        when(role.getName()).thenReturn(UserRole.ROLE_USER);

        when(userService.getUserByEmail(email)).thenReturn(user);
        when(passwordEncoder.matches(TEST_PASSWORD, "encodedPassword")).thenReturn(true);
        when(jwtTokenService.generateToken(email, UserRole.ROLE_USER)).thenReturn(TEST_TOKEN);

        // when
        AuthResponse result = authFacade.login(request);

        // then
        assertThat(result.token()).isEqualTo(TEST_TOKEN);
        verify(userService).getUserByEmail(email);
        verify(userService).validateUserStatus(user);
        verify(passwordEncoder).matches(TEST_PASSWORD, "encodedPassword");
        verify(jwtTokenService).generateToken(email, UserRole.ROLE_USER);
    }

    @Test
    @DisplayName("login — должен выбросить BadCredentialsException при неверном пароле")
    void login_shouldThrowBadCredentialsException_whenPasswordIsWrong() {
        // given
        LoginRequest request = new LoginRequest(email, TEST_PASSWORD);

        User user = mock(User.class);
        when(user.getPassword()).thenReturn("encodedPassword");
        when(userService.getUserByEmail(email)).thenReturn(user);
        when(passwordEncoder.matches(TEST_PASSWORD, "encodedPassword")).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authFacade.login(request))
                .isInstanceOf(BadCredentialsException.class);

        verify(userService).validateUserStatus(user);
        verifyNoInteractions(jwtTokenService);
    }

    @Test
    @DisplayName("login — должен выбросить NotFoundException если пользователь не найден")
    void login_shouldThrowNotFoundException_whenUserNotFound() {
        // given
        LoginRequest request = new LoginRequest(email, TEST_PASSWORD);
        when(userService.getUserByEmail(email)).thenThrow(NotFoundException.userByEmail(email));

        // when & then
        assertThatThrownBy(() -> authFacade.login(request))
                .isInstanceOf(NotFoundException.class);

        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtTokenService);
    }

    // ===== register =====

    @Test
    @DisplayName("register — должен создать пользователя и вернуть токен")
    void register_shouldCreateUserAndReturnToken() {
        // given
        RegisterRequest request = new RegisterRequest(email, TEST_PASSWORD);

        Role role = mock(Role.class);
        User user = mock(User.class);
        when(user.getEmail()).thenReturn(email);
        when(user.getRole()).thenReturn(role);
        when(role.getName()).thenReturn(UserRole.ROLE_USER);

        when(roleService.getRoleByName(UserRole.ROLE_USER)).thenReturn(role);
        when(userService.createUser(email, TEST_PASSWORD, role)).thenReturn(user);
        when(jwtTokenService.generateToken(email, UserRole.ROLE_USER)).thenReturn(TEST_TOKEN);

        // when
        AuthResponse result = authFacade.register(request);

        // then
        assertThat(result.token()).isEqualTo(TEST_TOKEN);
        verify(roleService).getRoleByName(UserRole.ROLE_USER);
        verify(userService).createUser(email, TEST_PASSWORD, role);
        verify(jwtTokenService).generateToken(email, UserRole.ROLE_USER);
    }
}