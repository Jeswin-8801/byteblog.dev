package com.jeswin8801.byteBlog.util.exceptions.enums;

import lombok.Getter;

@Getter
public enum BlogExceptions {
    BLOG_WITH_HEADER_ALREADY_EXISTS("A blog with the given header already exists");

    private final String message;

    BlogExceptions(String message) {
        this.message = message;
    }
}
