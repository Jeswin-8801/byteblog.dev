package com.jeswin8801.byteBlog.service.webapp.user.abstracts;

import com.jeswin8801.byteBlog.entities.dto.GenericResponseDto;
import com.jeswin8801.byteBlog.entities.dto.MessageResponseDto;
import com.jeswin8801.byteBlog.entities.dto.user.ChangePasswordRequestDto;
import com.jeswin8801.byteBlog.entities.dto.user.UserDto;

public interface UserService {

    UserDto findUserByEmail(String email);

    String getUserIdFromEmail(String email);

    void createUser(UserDto userDto);

    void updateUser(UserDto userDTO);

    GenericResponseDto<MessageResponseDto> updatePassword(ChangePasswordRequestDto changePasswordRequestDto);

    GenericResponseDto<MessageResponseDto> deleteUser(String id);
}
