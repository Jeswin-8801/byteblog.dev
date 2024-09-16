package com.jeswin8801.byteBlog.service.auth;

import com.jeswin8801.byteBlog.config.ApplicationProperties;
import com.jeswin8801.byteBlog.entities.dto.GenericResponseDto;
import com.jeswin8801.byteBlog.entities.dto.MessageResponseDto;
import com.jeswin8801.byteBlog.entities.dto.auth.requests.LoginRequestDto;
import com.jeswin8801.byteBlog.entities.dto.auth.requests.RegisterUserRequestDto;
import com.jeswin8801.byteBlog.entities.dto.auth.requests.ResetPasswordRequestDto;
import com.jeswin8801.byteBlog.entities.dto.auth.requests.VerifyEmailRequestDto;
import com.jeswin8801.byteBlog.entities.dto.auth.response.AuthResponseDto;
import com.jeswin8801.byteBlog.entities.dto.user.UserDto;
import com.jeswin8801.byteBlog.entities.model.User;
import com.jeswin8801.byteBlog.entities.model.enums.AuthProvider;
import com.jeswin8801.byteBlog.service.auth.abstracts.AuthenticationService;
import com.jeswin8801.byteBlog.service.auth.abstracts.RefreshTokenService;
import com.jeswin8801.byteBlog.service.mail.MailType;
import com.jeswin8801.byteBlog.service.mail.abstracts.EmailService;
import com.jeswin8801.byteBlog.service.webapp.user.abstracts.UserService;
import com.jeswin8801.byteBlog.util.AppUtil;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Objects;

@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Override
    public AuthResponseDto loginUser(LoginRequestDto loginRequestDto) {

        try {

            User user = userService.findUserByEmail(loginRequestDto.getEmail());
            // check for if an account with the given email exists
            if (Objects.isNull(user))
                throw new ResourceNotFoundException(AuthExceptions.USER_WITH_EMAIL_NOT_FOUND.getMessage());

            if (!user.isEmailVerified())
                throw new ByteBlogException(AuthExceptions.ACCOUNT_NOT_ACTIVATED.getMessage(), HttpStatus.BAD_REQUEST);

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
        sendMailVerifyEmail(userDto.getEmail());

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
    public GenericResponseDto<MessageResponseDto> logoutUser(String id) {
        if (!StringUtils.hasText(id))
            throw new ByteBlogException("No user-id provided", HttpStatus.BAD_REQUEST);
        User user = userService.findUserById(id);
        String refreshToken = user.getRefreshToken().getRefreshToken();
        refreshTokenService.blacklistRefreshToken(refreshToken);

        refreshTokenService.deleteRefreshTokenFromRefreshTokenTable(refreshToken, user);

        return GenericResponseDto.<MessageResponseDto>builder()
                .message(
                        MessageResponseDto.builder().message("Successfully logged out user").build()
                )
                .httpStatusCode(HttpStatus.OK)
                .build();
    }

    @Override
    public void sendMailVerifyEmail(String email) {
        User user = userService.findUserByEmail(email);

        if (Objects.isNull(user))
            throw new ResourceNotFoundException(AuthExceptions.USER_WITH_EMAIL_NOT_FOUND.getMessage());

        String verificationCode = AppUtil.generateRandomAlphaNumericString(20);
        long verificationCodeExpirationSeconds = applicationProperties.getMail().getVerificationCodeExpirationSeconds();

        user.setVerificationCodeExpiresAt(Instant.now().plusSeconds(verificationCodeExpirationSeconds));
        user.setVerificationCode(verificationCode);

        MultiValueMap<String, String> appendQueryParamsToVerificationLink =
                constructEmailVerificationLinkQueryParams(email, verificationCode);

        userService.saveUser(user);

        emailService.sendMail(MailType.VERIFY_EMAIL, email, appendQueryParamsToVerificationLink);
    }

    @Override
    public void sendMailPasswordReset(String email) {
        User user = userService.findUserByEmail(email);

        if (Objects.isNull(user))
            throw new ResourceNotFoundException(AuthExceptions.USER_WITH_EMAIL_NOT_FOUND.getMessage());

        String verificationCode = AppUtil.generateRandomAlphaNumericString(20);
        long verificationCodeExpirationSeconds = applicationProperties.getMail().getVerificationCodeExpirationSeconds();

        user.setVerificationCodeExpiresAt(Instant.now().plusSeconds(verificationCodeExpirationSeconds));
        user.setVerificationCode(verificationCode);

        MultiValueMap<String, String> appendQueryParamsToVerificationLink =
                constructPasswordResetLinkQueryParams(email, verificationCode);

        userService.saveUser(user);

        emailService.sendMail(MailType.PASSWORD_RESET_EMAIL, email, appendQueryParamsToVerificationLink);
    }

    @Override
    public GenericResponseDto<MessageResponseDto> verifyEmailAddress(VerifyEmailRequestDto verifyEmailRequestDTO) {
        User user = userService.getUserWithCodeVerified(verifyEmailRequestDTO.getEmail(), verifyEmailRequestDTO.getVerificationCode());

        if (Objects.isNull(user))
            throw new ResourceNotFoundException(AuthExceptions.MATCHING_VERIFICATION_RECORD_NOT_FOUND.getMessage());

        user.setEmailVerified(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);

        userService.saveUser(user);

        return GenericResponseDto.<MessageResponseDto>builder()
                .message(
                        MessageResponseDto.builder().message("Email verification successful").build()
                )
                .httpStatusCode(HttpStatus.OK)
                .build();
    }

    @Override
    public GenericResponseDto<MessageResponseDto> verifyAndProcessPasswordResetRequest(ResetPasswordRequestDto resetPasswordRequestDTO) {
        User user = userService.getUserWithCodeVerified(resetPasswordRequestDTO.getEmail(), resetPasswordRequestDTO.getVerificationCode());

        if (Objects.isNull(user))
            throw new ResourceNotFoundException(AuthExceptions.MATCHING_VERIFICATION_RECORD_NOT_FOUND.getMessage());

        user.setEmailVerified(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);

        userService.saveUser(user);

        userService.updatePassword(resetPasswordRequestDTO.getNewPassword(), user.getId());

        return GenericResponseDto.<MessageResponseDto>builder()
                .message(
                        MessageResponseDto.builder().message("Email verification successful").build()
                )
                .httpStatusCode(HttpStatus.OK)
                .build();
    }

    private static MultiValueMap<String, String> constructEmailVerificationLinkQueryParams(String email,
                                                                                           String verificationCode) {
        MultiValueMap<String, String> appendQueryParams = new LinkedMultiValueMap<>();
        // Generated QueryParams for the verification link, must sync with VerifyEmailRequestDTO
        appendQueryParams.add("email", email);
        appendQueryParams.add("verificationCode", verificationCode);
        return appendQueryParams;
    }

    private static MultiValueMap<String, String> constructPasswordResetLinkQueryParams(String email,
                                                                                       String verificationCode) {
        MultiValueMap<String, String> appendQueryParams = new LinkedMultiValueMap<>();
        // Generated QueryParams for the password reset link, must sync with ResetPasswordRequestDTO
        appendQueryParams.add("email", email);
        appendQueryParams.add("verificationCode", verificationCode);
        return appendQueryParams;
    }
}
