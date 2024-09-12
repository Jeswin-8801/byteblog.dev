package com.jeswin8801.byteBlog.service.webapp.user.abstracts;

import com.jeswin8801.byteBlog.entities.dto.GenericResponseDto;
import com.jeswin8801.byteBlog.entities.dto.MessageResponseDto;
import com.jeswin8801.byteBlog.entities.dto.user.ChangePasswordRequestDto;
import com.jeswin8801.byteBlog.entities.dto.user.UserDto;
import com.jeswin8801.byteBlog.entities.model.User;

public interface UserService {

    User findUserByEmail(String email);

    User findUserById(String id);

    void createUser(UserDto userDto);

    void updateUser(UserDto userDTO);

    GenericResponseDto<MessageResponseDto> updatePassword(ChangePasswordRequestDto changePasswordRequestDto);

    GenericResponseDto<MessageResponseDto> deleteUser(String id);
}
