package com.jeswin8801.byteBlog.util.exceptions;

public class ByteBlogException extends RuntimeException {

    public ByteBlogException() {
    }

    public ByteBlogException(String message) {
        super(message);
    }

    public ByteBlogException(String message, Throwable cause) {
        super(message, cause);
    }

    public ByteBlogException(Throwable cause) {
        super(cause);
    }
}
