import { JsonProperty } from 'json-object-mapper';

export class CommentCompactDto {
  @JsonProperty({ name: 'blog-heading-uri' })
  blogHeadingUri?: string;

  comment?: string;

  @JsonProperty({ name: 'last-updated' })
  lastUpdated?: string;

  @JsonProperty({ name: 'reply-comments-author-profile-image-uris' })
  replyCommentAuthorProfileImageUris?: string[];
}
