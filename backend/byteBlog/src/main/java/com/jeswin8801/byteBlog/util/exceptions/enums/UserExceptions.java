package com.jeswin8801.byteBlog.util.exceptions.enums;

import lombok.Getter;

@Getter
public enum UserExceptions {

    USER_RECORD_NOT_FOUND("User not found"),
    INVALID_USER_ID("Invalid User Id"),
    USER_EMAIL_NOT_AVAILABLE("An account with the given email already exists"),
    USER_USERNAME_NOT_AVAILABLE("An account with the given username already exists"),
    PASSWORD_MISMATCH("Current password is incorrect"),
    INVALID_PASSWORD_UPDATE_REQUEST("Provided Password update request is incorrect");

    private final String message;

    UserExceptions(String message) {
        this.message = message;
    }
}
