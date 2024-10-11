package com.jeswin8801.byteBlog.entities.dto.blog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorCompactDto {

    @JsonProperty("full-name")
    private String fullName;

    private String username;

    @JsonProperty("profile-image-url")
    private String profileImageUrl;

}
