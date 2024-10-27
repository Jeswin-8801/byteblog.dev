package com.jeswin8801.byteBlog.entities.dto.comment;

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
public class CommentCompactDto {
    @JsonProperty("blog-heading-uri")
    private String blogHeadingUri;

    private String comment;

    @JsonProperty("last-updated")
    private Instant lastUpdated; // will be sorted by client

    @JsonProperty("reply-comments-author-profile-image-uris")
    private Set<String> replyCommentAuthorProfileImageUris;
}
