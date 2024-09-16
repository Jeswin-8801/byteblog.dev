import { JsonProperty } from 'json-object-mapper';

export class PasswordResetDto {
  email?: string;

  @JsonProperty({ name: 'verification-code' })
  verificationCode?: string;

  @JsonProperty({ name: 'new-password' })
  newPassword?: string;
}
