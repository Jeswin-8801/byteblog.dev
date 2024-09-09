import { JsonProperty } from 'json-object-mapper';
import { Role } from './roles';

export class User {
  id?: string;

  username?: string;

  @JsonProperty({ name: 'full-name' })
  fullName?: string;

  email?: string;

  @JsonProperty({ name: 'email-verified' })
  emailVerified?: string;

  @JsonProperty({ name: 'profile-image-url' })
  profileImageUrl?: string;

  @JsonProperty({ name: 'auth-provider' })
  authProvider?: string;

  @JsonProperty({ name: 'registered-provider-id' })
  registeredProviderId?: string;

  @JsonProperty({ type: Role })
  roles?: Role[];
}
