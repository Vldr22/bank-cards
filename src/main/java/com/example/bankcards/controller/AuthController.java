package com.example.bankcards.controller;

import com.example.bankcards.dto.CommonResponse;
import com.example.bankcards.dto.request.LoginRequest;
import com.example.bankcards.dto.request.RegisterRequest;
import com.example.bankcards.dto.response.AuthResponse;
import com.example.bankcards.service.facade.AuthFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthFacade authFacade;

    @PostMapping("/login")
    public CommonResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return CommonResponse.success(authFacade.login(request));
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return CommonResponse.success(authFacade.register(request));
    }

}
