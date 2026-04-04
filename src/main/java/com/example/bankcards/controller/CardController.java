package com.example.bankcards.controller;

import com.example.bankcards.constants.SuccessMessages;
import com.example.bankcards.dto.CommonResponse;
import com.example.bankcards.dto.PageResponse;
import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.service.facade.CardFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardFacade cardFacade;

    @GetMapping()
    public PageResponse<CardResponse> getAllCards(Pageable pageable) {
        return PageResponse.of(cardFacade.getAllMyCards(pageable));
    }

    @PostMapping("/transfer")
    public CommonResponse<String> transfer(@Valid @RequestBody TransferRequest request) {
        cardFacade.transfer(request);
        return CommonResponse.success(SuccessMessages.TRANSFER_SUCCESS);
    }

    @PostMapping("/{id}/block-request")
    public CommonResponse<String> blockCard(@PathVariable Long id) {
        cardFacade.requestBlock(id);
        return CommonResponse.success(SuccessMessages.BLOCK_REQUEST_SUCCESS);
    }

}
