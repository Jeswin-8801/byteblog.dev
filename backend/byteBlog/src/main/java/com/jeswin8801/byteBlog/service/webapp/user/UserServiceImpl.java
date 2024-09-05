package com.jeswin8801.byteBlog.service.webapp.user;

import com.jeswin8801.byteBlog.config.ApplicationProperties;
import com.jeswin8801.byteBlog.entities.converters.UserMapper;
import com.jeswin8801.byteBlog.entities.dto.user.UserDto;
import com.jeswin8801.byteBlog.entities.model.Role;
import com.jeswin8801.byteBlog.entities.model.User;
import com.jeswin8801.byteBlog.entities.model.enums.AuthProvider;
import com.jeswin8801.byteBlog.entities.model.enums.UserPrivilege;
import com.jeswin8801.byteBlog.repository.UserRepository;
import com.jeswin8801.byteBlog.security.util.SecurityUtil;
import com.jeswin8801.byteBlog.service.mail.EmailService;
import com.jeswin8801.byteBlog.service.webapp.user.abstracts.UserService;
import com.jeswin8801.byteBlog.util.exceptions.ResourceConflictException;
import com.jeswin8801.byteBlog.util.exceptions.ResourceNotFoundException;
import com.jeswin8801.byteBlog.util.exceptions.enums.UserExceptions;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper<UserDto> userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final ApplicationProperties properties;

    public UserServiceImpl(UserRepository userRepository, UserMapper<UserDto> userMapper, PasswordEncoder passwordEncoder, EmailService emailService, ApplicationProperties properties) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.properties = properties;
    }

    @Override
    public UserDto findUserByEmail(String email) {

        return userMapper.toDto(
                userRepository.findByEmail(email)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(UserExceptions.USER_RECORD_NOT_FOUND.getMessage())
                        ),
                UserDto.class
        );
    }

    @Override
    public String getUserIdFromEmail(String email) {

        return userRepository
                .findIdByEmail(email)
                .orElseThrow(
                        () -> new ResourceNotFoundException(UserExceptions.USER_RECORD_NOT_FOUND.getMessage())
                );
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
        if (userDto.getAuthProvider().equals(AuthProvider.LOCAL) &&
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
    }

    @Override
    public void updateUser(UserDto userDTO) {

        User userEntity = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(UserExceptions.USER_RECORD_NOT_FOUND.getMessage()));
        userEntity.setFullName(userDTO.getFullName());
        userEntity.setProfileImageUrl(userDTO.getProfileImageUrl());
        userRepository.save(userEntity);
    }
}
