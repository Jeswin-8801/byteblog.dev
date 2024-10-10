package com.jeswin8801.byteBlog.entities.dto.blog;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jeswin8801.byteBlog.entities.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDto {

    private String username;

    @JsonProperty("full-name")
    private String fullName;

    private String email;

    @JsonProperty("profile-image-url")
    private String profileImageUrl;

    private String about;
}
