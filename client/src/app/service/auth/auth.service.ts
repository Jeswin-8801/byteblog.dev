import { HttpBackend, HttpClient, HttpContext } from '@angular/common/http';
import { inject, Injectable, signal, WritableSignal } from '@angular/core';
import { Router } from '@angular/router';
import { JwtHelperService } from '@auth0/angular-jwt';
import { Observable, tap, timeout } from 'rxjs';
import { ObjectMapper } from 'json-object-mapper';

import { IS_PUBLIC } from '../../auth/auth.interceptor';
import { Login } from '../../auth/login/interfaces/login.interface';
import { LoginResponse } from '../../auth/login/types/login-response.type';
import { SignUp } from '../../auth/signup/interfaces/sign-up.interface';
import { SignUpResponse } from '../../auth/signup/types/sign-up-response.type';
import { SignUpSuccess } from '../../auth/signup/interfaces/sign-up-success.interface';
import { environment } from '../../../environments/environment';
import { AccessTokenDto } from '../../models/dtos/access-token-dto';
import { AppConstants } from '../../common/app.constants';
import { LoginSuccessDto } from '../../models/dtos/login-success-dto';
import { StandardResponseDto } from '../../models/dtos/standard-response-dto';
import { TokenClaimsUserDto } from '../../models/dtos/token-claims-user-dto';
import { VerifyEmailDto } from '../../models/dtos/verify-email-dto';
import { PasswordResetDto } from '../../models/dtos/password-reset-dto';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor() {}

  private readonly httpClient = inject(HttpClient);
  private readonly httpBackend = new HttpClient(inject(HttpBackend));
  private readonly router = inject(Router);
  private readonly jwtHelper = inject(JwtHelperService);
  private readonly CONTEXT = {
    context: new HttpContext().set(IS_PUBLIC, true),
  };

  get user(): WritableSignal<TokenClaimsUserDto | null> {
    return signal(
      this.getAccessToken()
        ? ObjectMapper.deserialize(
            TokenClaimsUserDto,
            this.jwtHelper.decodeToken(this.getAccessToken()!)['user']
          )
        : null
    );
  }

  isAuthenticated(): boolean {
    return !this.jwtHelper.isTokenExpired(this.getAccessToken());
  }

  isRefreshTokenValid(): boolean {
    return !this.jwtHelper.isTokenExpired(this.getRefreshToken());
  }

  login(body: Login): Observable<LoginResponse> {
    return this.httpClient
      .post<LoginResponse>(
        `${environment.apiUrl}/auth/login`,
        body,
        this.CONTEXT
      )
      .pipe(
        tap((data) => {
          const loginSuccessData = ObjectMapper.deserialize(
            LoginSuccessDto,
            data
          );
          this.storeTokens(
            AppConstants.ACCESS_TOKEN,
            loginSuccessData.accessToken!
          );
          this.storeTokens(
            AppConstants.REFRESH_TOKEN,
            loginSuccessData.refreshToken!
          );
          this.router.navigate(['/home']);
        })
      );
  }

  signup(body: SignUp): Observable<SignUpResponse> {
    return this.httpClient
      .post<SignUpResponse>(
        `${environment.apiUrl}/auth/sign-up`,
        body,
        this.CONTEXT
      )
      .pipe(
        timeout(5000),
        tap((data) => {
          const signUpSuccessData = data as SignUpSuccess;
          console.log(signUpSuccessData);
        })
      );
  }

  // this request will not be intercepted as it is sent using HttpBackend
  refreshToken(): Observable<AccessTokenDto> {
    const headers = { Authorization: `Bearer ${this.getRefreshToken()}` };
    return this.httpBackend.get<AccessTokenDto>(
      `${environment.apiUrl}/auth/token/refresh`,
      {
        headers: headers,
      }
    );
  }

  logout(type: string): Observable<StandardResponseDto> {
    return this.httpClient
      .get<StandardResponseDto>(
        `${environment.apiUrl}/auth/sign-out?id=` + this.user()?.id
      )
      .pipe(
        tap((data) => {
          const responseData = data as StandardResponseDto;
          console.log(responseData);
          const email = this.user()?.email;
          this.removeTokens();

          let params = {};
          if (type === AppConstants.LOGOUT)
            params = { loggedOut: 'Success', user: email };
          this.router.navigate(['/auth/login'], {
            queryParams: params,
          });
        })
      );
  }

  verifyEmail(body: VerifyEmailDto): Observable<StandardResponseDto> {
    return this.httpClient.post<StandardResponseDto>(
      `${environment.apiUrl}/auth/verify-email`,
      body,
      this.CONTEXT
    );
  }

  passwordReset(body: PasswordResetDto): Observable<StandardResponseDto> {
    return this.httpClient.post<StandardResponseDto>(
      `${environment.apiUrl}/auth/process-password-reset`,
      body,
      this.CONTEXT
    );
  }

  sendPasswordResetMail(email: string): Observable<any> {
    return this.httpClient.get(
      `${environment.apiUrl}/auth/send-mail-password-reset?email=` + email
    );
  }

  storeRefreshedToken(data: any) {
    const accessTokenDto = ObjectMapper.deserialize(AccessTokenDto, data);
    this.storeTokens(AppConstants.ACCESS_TOKEN, accessTokenDto.accessToken!);
    console.log('Successfully refreshed token');
  }

  removeTokens() {
    localStorage.removeItem(AppConstants.ACCESS_TOKEN);
    localStorage.removeItem(AppConstants.REFRESH_TOKEN);
  }

  storeTokens(tokenType: string, token: string): void {
    localStorage.setItem(tokenType, token);
  }

  getAccessToken() {
    return localStorage.getItem(AppConstants.ACCESS_TOKEN);
  }

  getRefreshToken() {
    return localStorage.getItem(AppConstants.REFRESH_TOKEN);
  }
}
