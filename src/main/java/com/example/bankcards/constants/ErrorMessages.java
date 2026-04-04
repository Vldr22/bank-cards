package com.example.bankcards.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorMessages {

    //USER
    public static final String USER_NOT_FOUND = "User not found";
    public static final String USER_ALREADY_EXISTS = "User already exists";
    public static final String USER_BLOCKED = "User is blocked";
    public static final String USER_DELETED = "User is deleted";

    //ROLE
    public static final String ROLE_NOT_FOUND = "Role not found";

    //CARD
    public static final String CARD_NOT_ACTIVE = "Card is not active";
    public static final String TRANSFER_SAME_CARD = "Cannot transfer to the same card";
    public static final String INSUFFICIENT_FUNDS = "Insufficient funds for transfer";
    public static final String BLOCK_ORDER_NOT_FOUND = "Block order not found for card";
    public static final String BLOCK_ORDER_ALREADY_EXISTS = "Block order already exists for card";
    public static final String CARD_FOR_ADMIN_NOT_ALLOWED = "Cannot create card for admin";

    //GENERAL
    public static final String DATA_INTEGRITY_ERROR = "Data integrity violation";
    public static final String UNEXPECTED_ERROR = "Unexpected error";
}
