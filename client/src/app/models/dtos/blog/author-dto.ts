import { JsonProperty } from 'json-object-mapper';

export class AuthorDto {
  email?: string;

  username?: string;

  @JsonProperty({ name: 'full-name' })
  fullName?: string;

  about?: string;

  @JsonProperty({ name: 'profile-image-url' })
  profileImageUrl?: string;
}
