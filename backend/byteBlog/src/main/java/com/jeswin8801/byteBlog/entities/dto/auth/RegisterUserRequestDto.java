package com.jeswin8801.byteBlog.entities.dto.auth;

import lombok.Data;

@Data
public class RegisterUserRequestDto {

    private String username;

    private String email;

    private String password;
}
