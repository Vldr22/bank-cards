package com.example.bankcards.controller;

import com.example.bankcards.constants.SuccessMessages;
import com.example.bankcards.dto.CommonResponse;
import com.example.bankcards.dto.PageResponse;
import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.service.facade.CardFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cards")

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardFacade cardFacade;

    @Operation(summary = "Постраничный список карт пользователя с пагинацией")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "401", description = "Токен отсутствует или истёк")
    })
    @GetMapping()
    public PageResponse<CardResponse> getAllCards(Pageable pageable) {
        return PageResponse.of(cardFacade.getAllMyCards(pageable));
    }

    @Operation(summary = "Перевод между своими картами")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Перевод выполнен"),
            @ApiResponse(responseCode = "400", description = "Перевод на ту же карту"),
            @ApiResponse(responseCode = "401", description = "Токен отсутствует или истёк"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена"),
            @ApiResponse(responseCode = "409", description = "Карта не активна"),
            @ApiResponse(responseCode = "422", description = "Недостаточно средств")
    })
    @PostMapping("/transfer")
    public CommonResponse<String> transfer(@Valid @RequestBody TransferRequest request) {
        cardFacade.transfer(request);
        return CommonResponse.success(SuccessMessages.TRANSFER_SUCCESS);
    }

    @Operation(summary = "Запрос на блокировку карты")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заявка создана"),
            @ApiResponse(responseCode = "401", description = "Токен отсутствует или истёк"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена"),
            @ApiResponse(responseCode = "409", description = "Карта не активна или заявка на блокировку уже существует")
    })
    @PostMapping("/{id}/block-request")
    public CommonResponse<String> blockCard(@PathVariable Long id) {
        cardFacade.requestBlock(id);
        return CommonResponse.success(SuccessMessages.BLOCK_REQUEST_SUCCESS);
    }

}
