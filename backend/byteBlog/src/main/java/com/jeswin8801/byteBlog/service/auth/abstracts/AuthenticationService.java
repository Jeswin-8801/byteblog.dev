package com.jeswin8801.byteBlog.service.auth.abstracts;

import com.jeswin8801.byteBlog.entities.dto.GenericResponseDto;
import com.jeswin8801.byteBlog.entities.dto.MessageResponseDto;
import com.jeswin8801.byteBlog.entities.dto.auth.AuthResponseDto;
import com.jeswin8801.byteBlog.entities.dto.auth.LoginRequestDto;
import com.jeswin8801.byteBlog.entities.dto.auth.RegisterUserRequestDto;

public interface AuthenticationService {
    AuthResponseDto loginUser(LoginRequestDto loginRequestDto);

    GenericResponseDto<MessageResponseDto> registerUser(RegisterUserRequestDto registerUserRequestDto);
}
