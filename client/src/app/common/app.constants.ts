import { environment } from '../../environments/environment';

export class AppConstants {
  private static OAUTH2_URL = environment.apiUrl + `/oauth2/authorize`;
  private static REDIRECT_URL = `?redirect_uri=http://localhost:4200`;
  private static PROVIDER_GOOGLE = '/google';
  private static PROVIDER_GITHUB = '/github';

  public static LOGIN = '/auth/login';
  public static SIGNUP = '/auth/sign-up';

  public static GOOGLE_OAUTH_URL =
    this.OAUTH2_URL + this.PROVIDER_GOOGLE + this.REDIRECT_URL;
  public static GITHUB_OAUTH_URL =
    this.OAUTH2_URL + this.PROVIDER_GITHUB + this.REDIRECT_URL;
}
