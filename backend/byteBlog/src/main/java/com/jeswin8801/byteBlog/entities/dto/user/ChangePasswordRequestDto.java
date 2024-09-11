package com.jeswin8801.byteBlog.entities.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChangePasswordRequestDto {
    private String id;

    @JsonProperty("current-password")
    private String currentPassword;

    @JsonProperty("new-password")
    private String newPassword;
}
