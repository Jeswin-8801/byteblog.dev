package com.jeswin8801.byteBlog.entities.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegisterUserRequestDto {

    @JsonProperty("full-name")
    private String fullName;

    private String email;

    private String password;
}
