package com.jeswin8801.byteBlog.service.auth.abstracts;

import com.jeswin8801.byteBlog.entities.dto.GenericResponseDto;
import com.jeswin8801.byteBlog.entities.dto.MessageResponseDto;
import com.jeswin8801.byteBlog.entities.dto.auth.requests.LoginRequestDto;
import com.jeswin8801.byteBlog.entities.dto.auth.requests.RegisterUserRequestDto;
import com.jeswin8801.byteBlog.entities.dto.auth.requests.ResetPasswordRequestDto;
import com.jeswin8801.byteBlog.entities.dto.auth.requests.VerifyEmailRequestDto;
import com.jeswin8801.byteBlog.entities.dto.auth.response.AuthResponseDto;

public interface AuthenticationService {
    AuthResponseDto loginUser(LoginRequestDto loginRequestDto);

    GenericResponseDto<MessageResponseDto> registerUser(RegisterUserRequestDto registerUserRequestDto);

    GenericResponseDto<MessageResponseDto> logoutUser(String id);

    void sendMailVerifyEmail(String email);

    void sendMailPasswordReset(String email);

    GenericResponseDto<MessageResponseDto> verifyEmailAddress(VerifyEmailRequestDto verifyEmailRequestDTO);

    GenericResponseDto<MessageResponseDto> verifyAndProcessPasswordResetRequest(ResetPasswordRequestDto resetPasswordRequestDTO);
}
