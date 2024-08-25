package com.jeswin8801.byteBlog.util.exceptions.enums;

import lombok.Getter;

public enum UserExceptions {

    USER_RECORD_NOT_FOUND("User not found"),
    USER_EMAIL_NOT_AVAILABLE("This email isn't available. An account with the given email already exists."),
    OLD_PASSWORD_DOESNT_MATCH("Old and New Password doesn't match"),
    MATCHING_VERIFICATION_RECORD_NOT_FOUND("Provided verification request doesn't seems correct"),
    INVALID_PASSWORD_RESET_REQUEST("Provided Password reset request doesn't seems correct");

    @Getter
    private final String message;

    UserExceptions(String message) {
        this.message = message;
    }
}
