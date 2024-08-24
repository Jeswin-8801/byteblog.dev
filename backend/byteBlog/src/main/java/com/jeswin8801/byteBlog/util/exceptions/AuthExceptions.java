package com.jeswin8801.byteBlog.util.exceptions;

import lombok.Getter;

public enum AuthExceptions {

    INCORRECT_LOGIN_CREDENTIALS("Bad Login - Invalid username or password"),
    ACCOUNT_NOT_ACTIVATED("Account not activated - Please verify your email or reprocess verification using forgot-password"),
    UNAUTHORIZED_ACCESS("Unauthorized - Insufficient authorization access");

    @Getter
    private final String message;

    AuthExceptions(String message) {
        this.message = message;
    }
}
