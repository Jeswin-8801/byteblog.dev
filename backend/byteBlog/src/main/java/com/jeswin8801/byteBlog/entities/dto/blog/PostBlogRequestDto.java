package com.jeswin8801.byteBlog.entities.dto.blog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostBlogRequestDto {

    private String header;

    private String description;

    private Set<String> tags;

    @JsonProperty("primary-tag")
    private String primaryTag;

    @JsonProperty("user-id")
    private String userId;
}
