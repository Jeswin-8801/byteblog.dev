package com.jeswin8801.byteBlog.util.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ResourceConflictException extends RuntimeException {
    public ResourceConflictException(String message) {
        super(message);
    }

    public ResourceConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceConflictException(Throwable cause) {
        super(cause);
    }
}
