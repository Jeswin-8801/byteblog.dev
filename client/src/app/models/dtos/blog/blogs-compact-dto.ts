import {
  CacheKey,
  Deserializer,
  JsonIgnore,
  JsonProperty,
} from 'json-object-mapper';
import { AuthorCompactDto } from './author-compact-dto';

export class BlogsCompactDto {
  id?: string;

  @JsonProperty({ name: 'time-since-creation' })
  timeSinceCreation?: string;

  heading?: string;

  description?: string;

  tags?: string[];

  @JsonProperty({ name: 'primary-tag' })
  primaryTag?: string;

  @JsonIgnore()
  author?: AuthorCompactDto;
}
