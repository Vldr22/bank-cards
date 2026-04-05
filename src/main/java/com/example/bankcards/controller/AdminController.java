package com.example.bankcards.controller;

import com.example.bankcards.constants.SuccessMessages;
import com.example.bankcards.dto.CommonResponse;
import com.example.bankcards.dto.PageResponse;
import com.example.bankcards.dto.request.CreateCardRequest;
import com.example.bankcards.dto.request.UpdateCardStatusRequest;
import com.example.bankcards.dto.request.UpdateUserStatusRequest;
import com.example.bankcards.dto.response.AdminCardResponse;
import com.example.bankcards.dto.response.CardBlockOrderResponse;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.service.facade.AdminFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminFacade adminFacade;

    //===== USERS =====
    @GetMapping("/users")
    public PageResponse<UserResponse> getAllUsers(Pageable pageable) {
        return PageResponse.of(adminFacade.getAllUsers(pageable));
    }

    @PatchMapping("/users/{id}/status")
    public CommonResponse<UserResponse> updateUserStatus(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserStatusRequest request
    ) {
        return CommonResponse.success(adminFacade.updateUserStatus(id, request));
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
       adminFacade.deleteUser(id);
    }

    // ===== CARDS =====
    @GetMapping("/cards")
    public PageResponse<AdminCardResponse> getAllCards(Pageable pageable) {
        return PageResponse.of(adminFacade.getAllCards(pageable));
    }

    @PostMapping("/cards")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<AdminCardResponse> createCard(@Valid @RequestBody CreateCardRequest request) {
        return CommonResponse.success(adminFacade.createCard(request));
    }

    @PatchMapping("/cards/{cardId}/status")
    public CommonResponse<AdminCardResponse> updateCardStatus(
            @PathVariable Long cardId,
            @RequestBody UpdateCardStatusRequest request) {
        return CommonResponse.success(adminFacade.updateCardStatus(cardId, request));
    }

    @DeleteMapping("/cards/{cardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(@PathVariable Long cardId) {
        adminFacade.deleteCard(cardId);
    }

    // ===== BLOCK ORDERS =====
    @GetMapping("/block-orders")
    public PageResponse<CardBlockOrderResponse> getAllCardBlockOrders(Pageable pageable) {
        return PageResponse.of(adminFacade.getBlockOrders(pageable));
    }

    @PatchMapping("/block-orders/{cardId}/approve")
    public CommonResponse<String> approveBlockOrder(@PathVariable Long cardId) {
        adminFacade.approveBlockOrder(cardId);
        return CommonResponse.success(SuccessMessages.BLOCK_ORDER_APPROVED);
    }

    @PatchMapping("/block-orders/{cardId}/reject")
    public CommonResponse<String> rejectBlockOrder(@PathVariable Long cardId) {
        adminFacade.rejectBlockOrder(cardId);
        return CommonResponse.success(SuccessMessages.BLOCK_ORDER_REJECTED);
    }

}
