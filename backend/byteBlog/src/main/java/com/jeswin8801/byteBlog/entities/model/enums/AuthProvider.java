package com.jeswin8801.byteBlog.entities.model.enums;

import lombok.Getter;

@Getter
public enum AuthProvider {
    LOCAL("local"),
    GOOGLE("google"),
    GITHUB("github");

    private final String provider;

    AuthProvider(String provider) {
        this.provider = provider;
    }
}
