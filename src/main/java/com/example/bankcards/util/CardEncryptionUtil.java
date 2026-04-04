package com.example.bankcards.util;

import com.example.bankcards.constants.EncryptionConstants;
import com.example.bankcards.constants.SecurityErrorMessages;
import com.example.bankcards.properties.EncryptionProperties;
import io.jsonwebtoken.io.Decoders;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties(EncryptionProperties.class)
public class CardEncryptionUtil {

    private final TextEncryptor encryptor;

    public CardEncryptionUtil(EncryptionProperties properties) {
        byte[] keyBytes = Decoders.BASE64.decode(properties.getKey());

        if (keyBytes.length < EncryptionConstants.MIN_KEY_LENGTH) {
            throw new SecurityException(SecurityErrorMessages.WEAK_KEY);
        }

        this.encryptor = Encryptors.text(properties.getKey(), properties.getSalt());
    }

    public String encrypt(String cardNumber) {
        return encryptor.encrypt(cardNumber);
    }

    public String decrypt(String encryptedCardNumber) {
        return encryptor.decrypt(encryptedCardNumber);
    }

    public String mask(String encryptedCardNumber) {
        String cardNumber = decrypt(encryptedCardNumber);
        return EncryptionConstants.ENCRYPTION_MASK_PATTERN + cardNumber.substring(cardNumber.length() - 4);
    }

}
