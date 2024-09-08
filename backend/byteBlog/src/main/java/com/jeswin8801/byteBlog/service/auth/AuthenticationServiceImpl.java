package com.jeswin8801.byteBlog.service.auth;

import com.jeswin8801.byteBlog.entities.dto.GenericResponseDto;
import com.jeswin8801.byteBlog.entities.dto.MessageResponseDto;
import com.jeswin8801.byteBlog.entities.dto.auth.AuthResponseDto;
import com.jeswin8801.byteBlog.entities.dto.auth.LoginRequestDto;
import com.jeswin8801.byteBlog.entities.dto.auth.RegisterUserRequestDto;
import com.jeswin8801.byteBlog.entities.dto.user.UserDto;
import com.jeswin8801.byteBlog.entities.model.enums.AuthProvider;
import com.jeswin8801.byteBlog.security.jwt.JWTTokenProvider;
import com.jeswin8801.byteBlog.service.auth.abstracts.AuthenticationService;
import com.jeswin8801.byteBlog.service.webapp.user.abstracts.UserService;
import com.jeswin8801.byteBlog.util.exceptions.enums.AuthExceptions;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JWTTokenProvider jwtTokenProvider;

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager,
                                     UserService userService,
                                     JWTTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public AuthResponseDto loginUser(LoginRequestDto loginRequestDto) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getEmail(),
                            loginRequestDto.getPassword()
                    )
            );

            return AuthResponseDto.builder()
                    .accessToken(
                            jwtTokenProvider.generateJWTAccessToken(authentication)
                    )
                    .build();
        } catch (AuthenticationException exception) {
            if (exception instanceof DisabledException)
                throw new BadCredentialsException(AuthExceptions.ACCOUNT_NOT_ACTIVATED.getMessage());
            throw new BadCredentialsException(exception.getMessage());
        }
    }

    @Override
    public GenericResponseDto<MessageResponseDto> registerUser(RegisterUserRequestDto registerUserRequestDto) {
        UserDto userDto = new UserDto();
        userDto.setUsername(registerUserRequestDto.getUsername());
        userDto.setEmail(registerUserRequestDto.getEmail());
        userDto.setPassword(registerUserRequestDto.getPassword());
        userDto.setAuthProvider(AuthProvider.LOCAL.getProvider());

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
}
