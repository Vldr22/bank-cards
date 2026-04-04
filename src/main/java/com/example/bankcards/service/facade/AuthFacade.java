package com.example.bankcards.service.facade;

import com.example.bankcards.constants.SecurityErrorMessages;
import com.example.bankcards.dto.request.LoginRequest;
import com.example.bankcards.dto.request.RegisterRequest;
import com.example.bankcards.dto.response.AuthResponse;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.UserRole;
import com.example.bankcards.security.JwtTokenService;
import com.example.bankcards.service.RoleService;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthResponse login(LoginRequest request) {
        User user = userService.getUserByEmail(request.email());
        userService.validateUserStatus(user);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException(SecurityErrorMessages.INVALID_CREDENTIALS);
        }

        return generateTokenAndBuildAuthResponse(user);
    }

    public AuthResponse register(RegisterRequest request) {
        Role role = roleService.getRoleByName(UserRole.ROLE_USER);
        User user = userService.createUser(request.email(), request.password(), role);

        log.info("User registered successfully with email: {}", request.email());
        return generateTokenAndBuildAuthResponse(user);
    }


    private AuthResponse generateTokenAndBuildAuthResponse(User user) {
        String token = jwtTokenService.generateToken(
                user.getEmail(),
                user.getRole().getName()
        );
        return new AuthResponse(token);
    }

}
