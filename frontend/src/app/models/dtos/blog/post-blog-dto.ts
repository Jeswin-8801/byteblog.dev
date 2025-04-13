import { JsonProperty } from 'json-object-mapper';

export class PostBlogDto {
  header?: string;

  description?: string;

  tags?: string[];

  @JsonProperty({ name: 'primary-tag' })
  primaryTag?: string;

  @JsonProperty({ name: 'user-id' })
  userId?: string;
}
