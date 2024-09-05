package com.jeswin8801.byteBlog.service.auth.abstracts;

import com.jeswin8801.byteBlog.entities.dto.GenericResponseDto;
import com.jeswin8801.byteBlog.entities.dto.auth.AuthResponseDto;
import com.jeswin8801.byteBlog.entities.dto.auth.LoginRequestDto;
import com.jeswin8801.byteBlog.entities.dto.auth.RegisterUserRequestDto;
import com.jeswin8801.byteBlog.entities.dto.user.UserDto;

public interface AuthenticationService {
    AuthResponseDto loginUser(LoginRequestDto loginRequestDto);

    GenericResponseDto<String> registerUser(RegisterUserRequestDto registerUserRequestDto);
}
