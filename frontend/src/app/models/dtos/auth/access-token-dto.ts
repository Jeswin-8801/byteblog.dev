import { JsonProperty } from 'json-object-mapper';

export class AccessTokenDto {
  @JsonProperty({ name: 'access-token' })
  accessToken?: string;
}
