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
public class CommentDto {

    private String comment;

    @JsonProperty("last-updated")
    private Instant lastUpdated; // will be sorted by client

    @JsonProperty("child-reply-comments")
    private Set<CommentDto> childReplyComments;
}
