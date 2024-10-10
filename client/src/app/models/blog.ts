import { JsonProperty } from 'json-object-mapper';
import { Comment } from './comment';

export class Blog {
  id?: string;

  @JsonProperty({ name: 'time-since-creation' })
  timeSinceCreation?: string;

  heading?: string;

  description?: string;

  tags?: string[];

  @JsonProperty({ name: 'primary-tag' })
  primaryTag?: string;

  @JsonProperty({ name: 'markdown-file-url' })
  markdownFileUrl?: string;

  images?: string[];

  comments?: Comment[];
}
