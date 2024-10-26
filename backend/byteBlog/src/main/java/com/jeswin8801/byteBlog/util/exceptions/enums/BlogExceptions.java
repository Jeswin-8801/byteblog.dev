package com.jeswin8801.byteBlog.util.exceptions.enums;

import lombok.Getter;

@Getter
public enum BlogExceptions {
    BLOG_WITH_HEADER_ALREADY_EXISTS("A blog with the given header already exists"),
    NO_BLOGS_FOUND("No blogs found"),
    INVALID_BLOG_ID("No blog found with given ID"),
    NO_COMMENTS_FOUND("No comments Found"),
    INVALID_COMMENT_ID("Invalid comment Id provided");

    private final String message;

    BlogExceptions(String message) {
        this.message = message;
    }
}
