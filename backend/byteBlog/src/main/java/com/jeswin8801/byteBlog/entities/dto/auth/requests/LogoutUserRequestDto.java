package com.jeswin8801.byteBlog.entities.dto.auth.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogoutUserRequestDto {

    private String id;

    @JsonProperty("refresh-token")
    private String refreshToken;
}
