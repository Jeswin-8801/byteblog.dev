package com.jeswin8801.byteBlog.service.webapp.user;

import com.jeswin8801.byteBlog.config.ApplicationProperties;
import com.jeswin8801.byteBlog.entities.converters.UserMapper;
import com.jeswin8801.byteBlog.entities.dto.GenericResponseDto;
import com.jeswin8801.byteBlog.entities.dto.MessageResponseDto;
import com.jeswin8801.byteBlog.entities.dto.user.ChangePasswordRequestDto;
import com.jeswin8801.byteBlog.entities.dto.user.UserDto;
import com.jeswin8801.byteBlog.entities.model.RefreshToken;
import com.jeswin8801.byteBlog.entities.model.RefreshTokenBlacklist;
import com.jeswin8801.byteBlog.entities.model.Role;
import com.jeswin8801.byteBlog.entities.model.User;
import com.jeswin8801.byteBlog.entities.model.enums.AuthProvider;
import com.jeswin8801.byteBlog.entities.model.enums.UserPrivilege;
import com.jeswin8801.byteBlog.repository.RefreshTokenRepository;
import com.jeswin8801.byteBlog.repository.TokenBlacklistRepository;
import com.jeswin8801.byteBlog.repository.UserRepository;
import com.jeswin8801.byteBlog.security.jwt.JWTTokenProvider;
import com.jeswin8801.byteBlog.security.jwt.TokenType;
import com.jeswin8801.byteBlog.security.util.SecurityUtil;
import com.jeswin8801.byteBlog.service.mail.EmailService;
import com.jeswin8801.byteBlog.service.webapp.user.abstracts.UserService;
import com.jeswin8801.byteBlog.util.exceptions.ByteBlogException;
import com.jeswin8801.byteBlog.util.exceptions.ResourceConflictException;
import com.jeswin8801.byteBlog.util.exceptions.ResourceNotFoundException;
import com.jeswin8801.byteBlog.util.exceptions.enums.UserExceptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private TokenBlacklistRepository tokenBlacklistRepository;

    @Autowired
    private JWTTokenProvider jwtTokenProvider;

    @Autowired
    private UserMapper<UserDto> userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ApplicationProperties properties;

    @Override
    public User findUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElse(null);
    }

    @Override
    public User findUserById(String id) {
        return userRepository
                .findById(id)
                .orElse(null);
    }

    @Override
    @Transactional
    public void createUser(UserDto userDto) {

        if (ObjectUtils.isEmpty(userDto.getRoles())) {
            userDto.setRoles(
                    Set.of(new Role(
                            UserPrivilege.valueOf(SecurityUtil.ROLE_DEFAULT)
                    ))
            );
        }

        // check for if Auth provider is local (jwt auth) is present, and if so password is processed
        if (userDto.getAuthProvider().equals(AuthProvider.LOCAL.getProvider()) &&
                StringUtils.hasText(userDto.getPassword())) {
            userDto.setPassword(
                    passwordEncoder.encode(userDto.getPassword())
            );
        }

        // check for if an account already exists with the given username
        if (userRepository.existsByUsername(userDto.getUsername()))
            throw new ResourceConflictException(UserExceptions.USER_USERNAME_NOT_AVAILABLE.getMessage());

        // check for if an account already exists with the given email
        if (userRepository.existsByEmail(userDto.getEmail()))
            throw new ResourceConflictException(UserExceptions.USER_EMAIL_NOT_AVAILABLE.getMessage());

        // else, create user
        User userEntity = userMapper.toEntity(userDto);
        userRepository.save(userEntity);

        log.info("User {} created successfully", userDto.getEmail());
    }

    @Override
    public void updateUser(UserDto userDTO) {

        User userEntity = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(UserExceptions.USER_RECORD_NOT_FOUND.getMessage()));
        userEntity.setFullName(userDTO.getFullName());
        userEntity.setProfileImageUrl(userDTO.getProfileImageUrl());
        userRepository.save(userEntity);
    }

    @Override
    public GenericResponseDto<MessageResponseDto> updatePassword(ChangePasswordRequestDto changePasswordRequestDto) {

        User user = userRepository.findById(
                        changePasswordRequestDto.getId()
                )
                .orElse(null);

        if (Objects.isNull(user)) {
            log.error("No user found with the id: {}", changePasswordRequestDto.getId());
            throw new ByteBlogException(UserExceptions.INVALID_PASSWORD_RESET_REQUEST.getMessage(), HttpStatus.BAD_REQUEST);
        }

        // check if current password and the password provided match
        if (
                !passwordEncoder.matches(changePasswordRequestDto.getCurrentPassword(), user.getPassword())
        ) {
            log.error("Password mismatch");
            throw new ByteBlogException(UserExceptions.PASSWORD_MISMATCH.getMessage(), HttpStatus.BAD_REQUEST);
        }

        userRepository.updatePassword(
                passwordEncoder.encode(changePasswordRequestDto.getNewPassword()),
                changePasswordRequestDto.getId()
        );

        return new GenericResponseDto<>(MessageResponseDto.builder().message("Password updated successfully").build(), HttpStatus.OK);
    }

    @Override
    public GenericResponseDto<MessageResponseDto> deleteUser(String id) {
        // check if id exists
        User user = userRepository.findById(id).orElse(null);

        if (Objects.isNull(user)) {
            log.error("No user found with the given id");
            throw new ByteBlogException(UserExceptions.USER_RECORD_NOT_FOUND.getMessage(), HttpStatus.BAD_REQUEST);
        }

        // retrieve refresh token associated with user
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByUser(user);
        // blacklist token
        tokenBlacklistRepository.save(
                RefreshTokenBlacklist.builder()
                        .refreshToken(refreshTokenEntity.getRefreshToken())
                        .expiry(
                                jwtTokenProvider.getTokenExpiry(refreshTokenEntity.getRefreshToken(), TokenType.REFRESH).toInstant()
                        )
                        .build()
        );

        userRepository.deleteById(id);

        return new GenericResponseDto<>(MessageResponseDto.builder().message("Account deleted successfully").build(), HttpStatus.OK);
    }
}
