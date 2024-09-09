package com.jeswin8801.byteBlog.util.exceptions.enums;

import lombok.Getter;

@Getter
public enum AuthExceptions {

    INCORRECT_LOGIN_CREDENTIALS("Incorrect email or password provided"),
    USER_WITH_EMAIL_NOT_FOUND("No user found with the given email. Consider creating an account"),
    ACCOUNT_NOT_ACTIVATED("Account not activated - Please verify your email or reprocess verification using forgot-password"),
    UNAUTHORIZED_ACCESS("Unauthorized - Insufficient authorization access");

    private final String message;

    AuthExceptions(String message) {
        this.message = message;
    }
}
