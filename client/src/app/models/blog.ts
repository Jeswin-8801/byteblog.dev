import { JsonIgnore, JsonProperty } from 'json-object-mapper';
import { Comment } from './comment';
import { AuthorCompactDto } from './dtos/blog/author-compact-dto';
import { AuthorDto } from './dtos/blog/author-dto';

export class Blog {
  id?: string;

  @JsonProperty({ name: 'created-on' }) // mapped property from server response object
  timeSinceCreation?: string;

  heading?: string;

  @JsonProperty({ name: 'heading-uri' })
  headingUri?: string;

  description?: string;

  tags?: string[];

  @JsonProperty({ name: 'primary-tag' })
  primaryTag?: string;

  @JsonProperty({ name: 'markdown-file-url' })
  markdownFileUrl?: string;

  @JsonIgnore()
  author?: AuthorCompactDto;

  images?: string[];

  @JsonIgnore()
  comments?: Comment[];
}
