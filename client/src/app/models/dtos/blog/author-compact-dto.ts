import { JsonProperty } from 'json-object-mapper';

export class AuthorCompactDto {
  email?: string;

  username?: string;

  @JsonProperty({ name: 'profile-image-url' })
  profileImageUrl?: string;
}
