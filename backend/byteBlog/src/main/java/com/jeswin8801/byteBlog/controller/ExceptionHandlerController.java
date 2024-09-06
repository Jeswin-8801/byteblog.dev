package com.jeswin8801.byteBlog.controller;

import com.jeswin8801.byteBlog.entities.dto.GenericResponseDto;
import com.jeswin8801.byteBlog.entities.dto.MessageResponseDto;
import com.jeswin8801.byteBlog.util.exceptions.ByteBlogException;
import com.jeswin8801.byteBlog.util.exceptions.ResourceConflictException;
import com.jeswin8801.byteBlog.util.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> resourceNotFoundException(final ResourceNotFoundException exception,
                                                       final HttpServletRequest request) {

        log.info("DataNotFoundException handled {} ", exception.getMessage());
        return GenericResponseDto.<MessageResponseDto>builder()
                .message(
                        MessageResponseDto.builder().message(
                                exception.getMessage()
                        ).build()
                )
                .httpStatusCode(HttpStatus.NOT_FOUND)
                .build().getResponseEntity();
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<?> resourceConflictException(final ResourceConflictException exception,
                                                       final HttpServletRequest request) {

        log.info("ResourceConflictException handled {} ", exception.getMessage());
        return GenericResponseDto.<MessageResponseDto>builder()
                .message(
                        MessageResponseDto.builder().message(
                                exception.getMessage()
                        ).build()
                )
                .httpStatusCode(HttpStatus.CONFLICT)
                .build().getResponseEntity();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> methodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException exception,
                                                                 final HttpServletRequest request) {

        log.info("MethodArgumentTypeMismatchException handled {} ", exception.getMessage());
        return GenericResponseDto.<MessageResponseDto>builder()
                .message(
                        MessageResponseDto.builder().message(
                                exception.getMessage()
                        ).build()
                )
                .httpStatusCode(HttpStatus.BAD_REQUEST)
                .build().getResponseEntity();
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> badCredentialsException(final BadCredentialsException exception,
                                                     final HttpServletRequest request) {

        log.info("BadCredentialsException handled {} ", exception.getMessage());
        return GenericResponseDto.<MessageResponseDto>builder()
                .message(
                        MessageResponseDto.builder().message(
                                exception.getMessage()
                        ).build()
                )
                .httpStatusCode(HttpStatus.BAD_REQUEST)
                .build().getResponseEntity();
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> badRequestException(final BadCredentialsException exception,
                                                     final HttpServletRequest request) {

        log.info("BadRequestException handled {} ", exception.getMessage());
        return GenericResponseDto.<MessageResponseDto>builder()
                .message(
                        MessageResponseDto.builder().message(
                                exception.getMessage()
                        ).build()
                )
                .httpStatusCode(HttpStatus.BAD_REQUEST)
                .build().getResponseEntity();
    }

    @ExceptionHandler(ByteBlogException.class)
    public ResponseEntity<?> globalAppException(final ByteBlogException exception,
                                                final HttpServletRequest request) {

        log.info("ByteBlogException handled {}", exception.getMessage());
        return GenericResponseDto.<MessageResponseDto>builder()
                .message(
                        MessageResponseDto.builder().message(
                                exception.getMessage()
                        ).build()
                )
                .httpStatusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .build().getResponseEntity();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> globalAppException(final RuntimeException exception,
                                                final HttpServletRequest request) {
        return GenericResponseDto.<MessageResponseDto>builder()
                .message(
                        MessageResponseDto.builder().message(
                                exception.getMessage()
                        ).build()
                )
                .httpStatusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .build().getResponseEntity();
    }
}
