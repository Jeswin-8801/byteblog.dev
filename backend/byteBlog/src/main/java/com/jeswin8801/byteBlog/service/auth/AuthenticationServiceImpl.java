package com.jeswin8801.byteBlog.service.auth;

import com.jeswin8801.byteBlog.entities.dto.GenericResponseDto;
import com.jeswin8801.byteBlog.entities.dto.MessageResponseDto;
import com.jeswin8801.byteBlog.entities.dto.auth.AuthResponseDto;
import com.jeswin8801.byteBlog.entities.dto.auth.LoginRequestDto;
import com.jeswin8801.byteBlog.entities.dto.auth.RegisterUserRequestDto;
import com.jeswin8801.byteBlog.entities.dto.user.UserDto;
import com.jeswin8801.byteBlog.entities.model.User;
import com.jeswin8801.byteBlog.entities.model.enums.AuthProvider;
import com.jeswin8801.byteBlog.service.auth.abstracts.AuthenticationService;
import com.jeswin8801.byteBlog.service.auth.abstracts.RefreshTokenService;
import com.jeswin8801.byteBlog.service.webapp.user.abstracts.UserService;
import com.jeswin8801.byteBlog.util.exceptions.ByteBlogException;
import com.jeswin8801.byteBlog.util.exceptions.ResourceNotFoundException;
import com.jeswin8801.byteBlog.util.exceptions.enums.AuthExceptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Override
    public AuthResponseDto loginUser(LoginRequestDto loginRequestDto) {

        try {

            User user = userService.findUserByEmail(loginRequestDto.getEmail());
            // check for if an account with the given email exists
            if (Objects.isNull(user))
                throw new ResourceNotFoundException(AuthExceptions.USER_WITH_EMAIL_NOT_FOUND.getMessage());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getEmail(),
                            loginRequestDto.getPassword()
                    )
            );

            return refreshTokenService.constructAuthResponseForLogin(authentication, user);
        } catch (AuthenticationException exception) {
            if (exception instanceof DisabledException)
                throw new BadCredentialsException(AuthExceptions.ACCOUNT_NOT_ACTIVATED.getMessage());
            else if (exception.getMessage().equals("Bad credentials"))
                throw new BadCredentialsException(AuthExceptions.INCORRECT_LOGIN_CREDENTIALS.getMessage());
            throw exception;
        }
    }

    @Override
    public GenericResponseDto<MessageResponseDto> registerUser(RegisterUserRequestDto registerUserRequestDto) {
        UserDto userDto = new UserDto();
        userDto.setUsername(registerUserRequestDto.getUsername());
        userDto.setEmail(registerUserRequestDto.getEmail());
        userDto.setPassword(registerUserRequestDto.getPassword());
        userDto.setAuthProvider(AuthProvider.LOCAL.getProvider());
        userDto.setOnline(true);
        userDto.setEmailVerified(false);
        userDto.setProfileImageUpdated(false);
        userDto.setRegisteredProviderId("0");

        userService.createUser(userDto);

        return GenericResponseDto.<MessageResponseDto>builder()
                .message(
                        MessageResponseDto.builder().message(
                                String.format("User '%s' created successfully", registerUserRequestDto.getUsername())
                        ).build()
                )
                .httpStatusCode(HttpStatus.CREATED)
                .build();
    }

    @Override
    public MessageResponseDto logoutUser(String id) {
        if (!StringUtils.hasText(id))
            throw new ByteBlogException("No user-id provided", HttpStatus.BAD_REQUEST);
        User user = userService.findUserById(id);
        String refreshToken = user.getRefreshToken().getRefreshToken();
        refreshTokenService.blacklistRefreshToken(refreshToken);

        refreshTokenService.deleteRefreshTokenFromRefreshTokenTable(refreshToken, user);

        return MessageResponseDto.builder().message("Successfully logged out user").build();
    }
}
