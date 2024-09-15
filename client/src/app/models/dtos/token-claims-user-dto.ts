import { JsonProperty } from 'json-object-mapper';
import { Role, RoleDeserializer } from '../role';

export class TokenClaimsUserDto {
  id?: string;

  username?: string;

  @JsonProperty({ name: 'full-name' })
  fullName?: string;

  email?: string;

  @JsonProperty({ name: 'profile-image-url' })
  profileImageUrl?: string;

  @JsonProperty({ name: 'is-online' })
  isOnline?: boolean;

  @JsonProperty({ name: 'auth-provider' })
  authProvider?: string;

  @JsonProperty({ type: Role, deserializer: RoleDeserializer })
  roles?: Role[];
}
