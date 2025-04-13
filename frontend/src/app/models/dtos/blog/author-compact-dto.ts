import { JsonProperty } from 'json-object-mapper';

export class AuthorCompactDto {
  @JsonProperty({ name: 'full-name' })
  fullName?: string;

  username?: string;

  @JsonProperty({ name: 'profile-image-url' })
  profileImageUrl?: string;
}
