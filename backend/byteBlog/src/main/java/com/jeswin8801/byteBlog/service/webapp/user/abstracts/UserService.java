package com.jeswin8801.byteBlog.service.webapp.user.abstracts;

import com.jeswin8801.byteBlog.entities.dto.GenericResponseDto;
import com.jeswin8801.byteBlog.entities.dto.MessageResponseDto;
import com.jeswin8801.byteBlog.entities.dto.user.ChangePasswordRequestDto;
import com.jeswin8801.byteBlog.entities.dto.user.UserDto;
import com.jeswin8801.byteBlog.entities.model.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    User findUserByEmail(String email);

    User findUserById(String id);

    GenericResponseDto<UserDto> getUser(String id);

    void createUser(UserDto userDto);

    GenericResponseDto<MessageResponseDto> updateUser(UserDto userDTO, MultipartFile image);

    void updateOauthUser(UserDto userDto);

    GenericResponseDto<MessageResponseDto> updatePassword(ChangePasswordRequestDto changePasswordRequestDto);

    GenericResponseDto<MessageResponseDto> deleteUser(String id);
}
