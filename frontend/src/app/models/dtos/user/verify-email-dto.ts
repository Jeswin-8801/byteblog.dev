import { JsonProperty } from 'json-object-mapper';

export class VerifyEmailDto {
  email?: string;

  @JsonProperty({ name: 'verification-code' })
  verificationCode?: string;
}
