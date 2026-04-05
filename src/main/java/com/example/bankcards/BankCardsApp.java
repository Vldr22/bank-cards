package com.example.bankcards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BankCardsApp {

    public static void main(String[] args) {
        SpringApplication.run(BankCardsApp.class, args);
    }
}
