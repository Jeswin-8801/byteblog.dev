package com.jeswin8801.byteBlog.entities.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogoutUserDto {

    private String id;

    @JsonProperty("refresh-token")
    private String refreshToken;
}
