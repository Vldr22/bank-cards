package com.example.bankcards.controller;

import com.example.bankcards.dto.CommonResponse;
import com.example.bankcards.dto.request.LoginRequest;
import com.example.bankcards.dto.request.RegisterRequest;
import com.example.bankcards.dto.response.AuthResponse;
import com.example.bankcards.service.facade.AuthFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth")
@SecurityRequirements

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthFacade authFacade;

    @Operation(summary = "Вход в систему")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешно — возвращает JWT токен"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
            @ApiResponse(responseCode = "401", description = "Неверный email или пароль"),
            @ApiResponse(responseCode = "403", description = "Аккаунт заблокирован или удалён")
    })
    @PostMapping("/login")
    public CommonResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return CommonResponse.success(authFacade.login(request));
    }

    @Operation(summary = "Регистрация пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь создан — возвращает JWT токен"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
            @ApiResponse(responseCode = "409", description = "Пользователь с таким email уже существует")
    })
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return CommonResponse.success(authFacade.register(request));
    }

}
