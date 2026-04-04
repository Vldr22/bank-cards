package com.example.bankcards.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CardNumberGenerator {

    public static String generate() {
        StringBuilder number = new StringBuilder();

        for (int i = 0; i < 15; i++) {
            number.append((int) (Math.random() * 10));
        }

        number.append(calculateCheckDigit(number.toString()));
        return number.toString();
    }

    private static int calculateCheckDigit(String number) {
        int sum = 0;
        for (int i = 0; i < number.length(); i++) {
            int digit = Character.getNumericValue(number.charAt(i));
            if (i % 2 == 0) {
                digit *= 2;
                if (digit > 9) digit -= 9;
            }
            sum += digit;
        }
        return (10 - sum % 10) % 10;
    }

}
