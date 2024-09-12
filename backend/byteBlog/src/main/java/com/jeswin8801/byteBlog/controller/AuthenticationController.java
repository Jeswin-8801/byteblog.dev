package com.jeswin8801.byteBlog.controller;

import com.jeswin8801.byteBlog.entities.dto.GenericResponseDto;
import com.jeswin8801.byteBlog.entities.dto.MessageResponseDto;
import com.jeswin8801.byteBlog.entities.dto.auth.*;
import com.jeswin8801.byteBlog.service.auth.abstracts.AuthenticationService;
import com.jeswin8801.byteBlog.service.auth.abstracts.RefreshTokenService;
import com.jeswin8801.byteBlog.service.webapp.user.abstracts.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequestDto requestDto) {
        return GenericResponseDto.<AuthResponseDto>builder()
                .message(
                        authenticationService.loginUser(requestDto)
                )
                .httpStatusCode(HttpStatus.OK)
                .build()
                .getResponseEntity();
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> registerUser(@RequestBody RegisterUserRequestDto requestDto) {
        return authenticationService.registerUser(requestDto).getResponseEntity();
    }

    @GetMapping("/sign-out")
    public ResponseEntity<?> logoutUser(@RequestParam String id) {
        return GenericResponseDto.<MessageResponseDto>builder()
                .message(
                        authenticationService.logoutUser(id)
                )
                .httpStatusCode(HttpStatus.OK)
                .build()
                .getResponseEntity();
    }

    @GetMapping("/token/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        return refreshTokenService.refreshToken(request).getResponseEntity();
    }

}
