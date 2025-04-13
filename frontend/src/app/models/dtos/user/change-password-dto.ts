import { JsonProperty } from 'json-object-mapper';

export class ChangePasswordDto {
  id?: string;

  @JsonProperty({ name: 'current-password' })
  currentPassword?: string;

  @JsonProperty({ name: 'new-password' })
  newPassword?: string;
}
