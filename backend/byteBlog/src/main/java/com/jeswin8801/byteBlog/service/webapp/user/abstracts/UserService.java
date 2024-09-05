package com.jeswin8801.byteBlog.service.webapp.user.abstracts;

import com.jeswin8801.byteBlog.entities.dto.user.UserDto;

public interface UserService {

    UserDto findUserByEmail(String email);

    String getUserIdFromEmail(String email);

    void    createUser(UserDto userDto);

    void updateUser(UserDto userDTO);
}
