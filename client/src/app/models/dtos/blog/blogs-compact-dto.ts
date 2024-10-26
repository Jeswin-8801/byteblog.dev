import {
  CacheKey,
  Deserializer,
  JsonIgnore,
  JsonProperty,
} from 'json-object-mapper';
import { AuthorCompactDto } from './author-compact-dto';

export class BlogsCompactDto {
  @JsonProperty({ name: 'created-on' }) // mapped property from server response object
  timeSinceCreation?: string;

  heading?: string;

  @JsonProperty({ name: 'heading-uri' })
  headingUri?: string;

  description?: string;

  tags?: string[];

  @JsonProperty({ name: 'primary-tag' })
  primaryTag?: string;

  @JsonIgnore()
  author?: AuthorCompactDto;
}
