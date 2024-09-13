import { JsonProperty } from 'json-object-mapper';

export class LoginSuccessDto {
  @JsonProperty({ name: 'access-token' })
  accessToken?: string;

  @JsonProperty({ name: 'refresh-token' })
  refreshToken?: string;
}
