import { JsonProperty } from 'json-object-mapper';

export class PostCommentDto {
  comment?: string;

  @JsonProperty({ name: 'user-id' })
  userId?: string;

  @JsonProperty({ name: 'blog-id' })
  blogId?: string;

  @JsonProperty({ name: 'parent-comment-id' })
  parentCommentId?: number;
}
