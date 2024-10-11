package com.jeswin8801.byteBlog.service.webapp.abstracts;

import com.jeswin8801.byteBlog.entities.dto.GenericResponseDto;
import com.jeswin8801.byteBlog.entities.dto.MessageResponseDto;
import com.jeswin8801.byteBlog.entities.dto.user.ChangePasswordRequestDto;
import com.jeswin8801.byteBlog.entities.dto.user.UserDto;
import com.jeswin8801.byteBlog.entities.model.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    User findUserByEmail(String email);

    User findUserById(String id);

    User findUserByUsername(String username);

    GenericResponseDto<UserDto> getUser(String id);

    void createUser(UserDto userDto);

    void saveUser(User user);

    GenericResponseDto<MessageResponseDto> updateUser(UserDto userDTO, MultipartFile image);

    void updateOauthUser(UserDto userDto);

    GenericResponseDto<MessageResponseDto> processChangePassword(ChangePasswordRequestDto changePasswordRequestDto);

    void updatePassword(String password, String id);

    GenericResponseDto<MessageResponseDto> deleteUser(String id);

    User getUserWithCodeVerified(String email, String verificationCode);
}
