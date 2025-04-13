import { JsonProperty } from 'json-object-mapper';
import { Role, RoleDeserializer } from './role';

export class User {
  id?: string;

  username?: string;

  @JsonProperty({ name: 'full-name' })
  fullName?: string;

  email?: string;

  @JsonProperty({ name: 'email-verified' })
  emailVerified?: boolean;

  @JsonProperty({ name: 'profile-image-url' })
  profileImageUrl?: string;

  about?: string;

  @JsonProperty({ name: 'is-online' })
  isOnline?: boolean;

  @JsonProperty({ name: 'auth-provider' })
  authProvider?: string;

  @JsonProperty({ name: 'registered-provider-id' })
  registeredProviderId?: string;

  @JsonProperty({ type: Role, deserializer: RoleDeserializer })
  roles?: Role[];
}
