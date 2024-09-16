package com.jeswin8801.byteBlog.entities.dto.auth.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResetPasswordRequestDto {

    private String email;

    @JsonProperty("verification-code")
    private String verificationCode;

    @JsonProperty("new-password")
    private String newPassword;
}
