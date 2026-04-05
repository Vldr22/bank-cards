package com.example.bankcards.sheduler;

import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardExpirationScheduler {

    private final CardService cardService;

    @Scheduled(cron = "${app.scheduler.card-expiration-cron}")
    public void doCardExpiration() {
        log.info("Card expiration scheduler started");
        int updated = cardService.expireOutdatedCards();
        log.info("Card expiration scheduler finished: cards been updated: {}", updated);
    }

}
