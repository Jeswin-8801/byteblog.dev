import { JsonProperty } from 'json-object-mapper';

export class Comment {
  comment?: string;

  @JsonProperty({ name: 'last-updated' })
  lastUpdated?: string; // will be sorted by client

  @JsonProperty({ name: 'child-reply-comments' })
  childReplyComments?: Comment[];
}
