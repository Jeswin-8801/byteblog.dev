package com.jeswin8801.byteBlog.service.auth.abstracts;

import com.jeswin8801.byteBlog.entities.dto.GenericResponseDto;
import com.jeswin8801.byteBlog.entities.dto.auth.AccessTokenDto;
import com.jeswin8801.byteBlog.entities.dto.auth.AuthResponseDto;
import com.jeswin8801.byteBlog.entities.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface RefreshTokenService {

    AuthResponseDto constructAuthResponseForLogin(Authentication authentication, User user);

    GenericResponseDto<AccessTokenDto> refreshToken(HttpServletRequest request);

    void deleteRefreshTokenFromRefreshTokenTable(String refreshToken, User user);

    void processRefreshToken(String refreshToken, User user);

    void blacklistRefreshToken(String refreshToken);
}
