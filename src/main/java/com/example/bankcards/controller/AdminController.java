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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin")

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminFacade adminFacade;

    //===== USERS =====
    @Operation(summary = "Получить постраничный список всех пользователей с пагинацией")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "401", description = "Токен отсутствует или истёк"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    @GetMapping("/users")
    public PageResponse<UserResponse> getAllUsers(Pageable pageable) {
        return PageResponse.of(adminFacade.getAllUsers(pageable));
    }

    @Operation(summary = "Изменить статус пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статус изменён"),
            @ApiResponse(responseCode = "401", description = "Токен отсутствует или истёк"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PatchMapping("/users/{id}/status")
    public CommonResponse<UserResponse> updateUserStatus(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserStatusRequest request
    ) {
        return CommonResponse.success(adminFacade.updateUserStatus(id, request));
    }

    @Operation(summary = "Удалить пользователя (через присвоение статуса DELETED)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь удалён"),
            @ApiResponse(responseCode = "401", description = "Токен отсутствует или истёк"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
       adminFacade.deleteUser(id);
    }

    // ===== CARDS =====

    @Operation(summary = "Получить постратичный список всех карт с пагинацией")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "401", description = "Токен отсутствует или истёк"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    @GetMapping("/cards")
    public PageResponse<AdminCardResponse> getAllCards(Pageable pageable) {
        return PageResponse.of(adminFacade.getAllCards(pageable));
    }

    @Operation(summary = "Создать карту")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Карта создана"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации или попытка создать карту для админа"),
            @ApiResponse(responseCode = "401", description = "Токен отсутствует или истёк"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PostMapping("/cards")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<AdminCardResponse> createCard(@Valid @RequestBody CreateCardRequest request) {
        return CommonResponse.success(adminFacade.createCard(request));
    }

    @Operation(summary = "Изменить статус карты")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статус изменён"),
            @ApiResponse(responseCode = "401", description = "Токен отсутствует или истёк"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена")
    })
    @PatchMapping("/cards/{cardId}/status")
    public CommonResponse<AdminCardResponse> updateCardStatus(
            @PathVariable Long cardId,
            @RequestBody UpdateCardStatusRequest request) {
        return CommonResponse.success(adminFacade.updateCardStatus(cardId, request));
    }

    @Operation(summary = "Удалить карту")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Карта удалена"),
            @ApiResponse(responseCode = "401", description = "Токен отсутствует или истёк"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена")
    })
    @DeleteMapping("/cards/{cardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(@PathVariable Long cardId) {
        adminFacade.deleteCard(cardId);
    }

    // ===== BLOCK ORDERS =====
    @Operation(summary = "Постраничный список зарегистрированных заявок на блокировку карт с пагинацией")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "401", description = "Токен отсутствует или истёк"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    @GetMapping("/block-orders")
    public PageResponse<CardBlockOrderResponse> getAllCardBlockOrders(Pageable pageable) {
        return PageResponse.of(adminFacade.getBlockOrders(pageable));
    }

    @Operation(summary = "Одобрить заявку на блокировку")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заявка одобрена, карта заблокирована"),
            @ApiResponse(responseCode = "401", description = "Токен отсутствует или истёк"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
            @ApiResponse(responseCode = "404", description = "Заявка не найдена")
    })
    @PatchMapping("/block-orders/{cardId}/approve")
    public CommonResponse<String> approveBlockOrder(@PathVariable Long cardId) {
        adminFacade.approveBlockOrder(cardId);
        return CommonResponse.success(SuccessMessages.BLOCK_ORDER_APPROVED);
    }

    @Operation(summary = "Отклонить заявку на блокировку")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заявка отклонена"),
            @ApiResponse(responseCode = "401", description = "Токен отсутствует или истёк"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
            @ApiResponse(responseCode = "404", description = "Заявка не найдена")
    })
    @PatchMapping("/block-orders/{cardId}/reject")
    public CommonResponse<String> rejectBlockOrder(@PathVariable Long cardId) {
        adminFacade.rejectBlockOrder(cardId);
        return CommonResponse.success(SuccessMessages.BLOCK_ORDER_REJECTED);
    }

}
