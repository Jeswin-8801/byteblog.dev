package com.jeswin8801.byteBlog.controller;

import com.jeswin8801.byteBlog.entities.dto.GenericResponseDto;
import com.jeswin8801.byteBlog.entities.dto.auth.requests.LoginRequestDto;
import com.jeswin8801.byteBlog.entities.dto.auth.requests.RegisterUserRequestDto;
import com.jeswin8801.byteBlog.entities.dto.auth.requests.ResetPasswordRequestDto;
import com.jeswin8801.byteBlog.entities.dto.auth.requests.VerifyEmailRequestDto;
import com.jeswin8801.byteBlog.entities.dto.auth.response.AuthResponseDto;
import com.jeswin8801.byteBlog.service.auth.abstracts.AuthenticationService;
import com.jeswin8801.byteBlog.service.auth.abstracts.RefreshTokenService;
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
        return authenticationService.logoutUser(id).getResponseEntity();
    }

    @GetMapping("/token/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        return refreshTokenService.refreshToken(request).getResponseEntity();
    }

    /**
     * For resending verify email in case the verification code expires
     */
    @GetMapping("/send-mail-verify-email")
    public void sendMailVerifyEmail(@RequestParam String email) {
        authenticationService.sendMailVerifyEmail(email);
    }

    @GetMapping("/send-mail-password-reset")
    public void sendMailPasswordReset(@RequestParam String email) {
        authenticationService.sendMailPasswordReset(email);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmailRequest(@RequestBody VerifyEmailRequestDto verifyEmailRequestDTO) {
        return authenticationService.verifyEmailAddress(verifyEmailRequestDTO).getResponseEntity();
    }

    @PostMapping("/process-password-reset")
    public ResponseEntity<?> verifyAndProcessPasswordResetRequest(@RequestBody ResetPasswordRequestDto resetPasswordRequestDTO) {
        return authenticationService.verifyAndProcessPasswordResetRequest(resetPasswordRequestDTO).getResponseEntity();
    }

}
