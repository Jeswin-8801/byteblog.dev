package com.jeswin8801.byteBlog.entities.dto.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentDto {

    @JsonProperty(required = true)
    private String comment;

    @JsonProperty(value = "user-id", required = true)
    private String userId;

    @JsonProperty(value = "blog-id", required = true)
    private String blogId;

    // can be null as the first comments associated with a blog has no parent comment and the Blog is the parent
    @JsonProperty("parent-comment-id")
    private Long parentCommentId;
}