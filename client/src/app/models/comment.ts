import { JsonProperty } from 'json-object-mapper';
import { AuthorCompactDto } from './dtos/blog/author-compact-dto';

export class Comment {
  id?: number;

  comment?: string;

  author?: AuthorCompactDto;

  @JsonProperty({ name: 'last-updated' })
  lastUpdated?: string;

  @JsonProperty({ name: 'child-reply-comments' })
  childReplyComments?: Comment[];
}
