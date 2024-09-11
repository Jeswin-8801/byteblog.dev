package com.jeswin8801.byteBlog.util.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
public class ByteBlogException extends RuntimeException {
    @Getter
    private HttpStatus httpStatus;

    public ByteBlogException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public ByteBlogException(String message, Throwable cause) {
        super(message, cause);
    }

    public ByteBlogException(Throwable cause) {
        super(cause);
    }
}
