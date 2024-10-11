package com.jeswin8801.byteBlog.entities.dto.blog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogsCompactResponseDto {

    @JsonProperty("created-on")
    private Instant createdOn;

    private String heading;

    @JsonProperty("heading-uri")
    private String headingUri;

    private String description;

    private Set<String> tags;

    @JsonProperty("primary-tag")
    private String primaryTag;

    private AuthorCompactDto author;
}
