package com.jeswin8801.byteBlog.entities.dto.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jeswin8801.byteBlog.entities.dto.blog.AuthorCompactDto;
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
public class CommentDto {

    private Long id;

    private String comment;

    private AuthorCompactDto author;

    @JsonProperty("last-updated")
    private Instant lastUpdated; // will be sorted by client

    @JsonProperty("child-reply-comments")
    private Set<CommentDto> childReplyComments;
}
