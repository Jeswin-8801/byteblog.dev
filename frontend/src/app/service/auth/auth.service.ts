import { Injectable, inject, signal, WritableSignal } from '@angular/core';
import { HttpClient, HttpContext } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { JwtHelperService } from '@auth0/angular-jwt';
import { catchError, tap } from 'rxjs/operators';

import { environment } from '../../../environments/environment';
import { Login } from '../../auth/login/interfaces/login.interface';
import { User } from '../../models/user';
import { LoginResponse } from '../../auth/login/types/login-response.type';
import { LoginSuccess } from '../../auth/login/interfaces/login-success.interface';
import { IS_PUBLIC } from '../../auth/auth.interceptor';
import { SignUp } from '../../auth/sign-up/interfaces/sign-up.interface';
import { SignUpResponse } from '../../auth/sign-up/types/sign-up-response.type';
import { SignUpSuccess } from '../../auth/sign-up/interfaces/sign-up-success.interface';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly jwtHelper = inject(JwtHelperService);
  private readonly CONTEXT = {
    context: new HttpContext().set(IS_PUBLIC, true),
  };

  get user(): WritableSignal<User | null> {
    const token = localStorage.getItem('token');
    return signal(token ? this.jwtHelper.decodeToken(token) : null);
  }

  isAuthenticated(): boolean {
    // if (!('token' in localStorage)) return false;
    return !this.jwtHelper.isTokenExpired();
  }

  login(body: Login): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(
        `${environment.apiUrl}/auth/login`,
        body,
        this.CONTEXT
      )
      .pipe(
        tap((data) => {
          const loginSuccessData = data as LoginSuccess;
          this.storeTokens(loginSuccessData);
          this.router.navigate(['/home']);
        })
      );
  }

  signup(body: SignUp): Observable<SignUpResponse> {
    return this.http
      .post<SignUpResponse>(
        `${environment.apiUrl}/auth/sign-up`,
        body,
        this.CONTEXT
      )
      .pipe(
        tap((data) => {
          const signUpSuccessData = data as SignUpSuccess;
          console.log(signUpSuccessData.message);
          this.router.navigate(['/auth/login']);
        })
      );
  }

  logout(): void {
    localStorage.removeItem('token');
    this.router.navigate(['/auth/login']);
  }

  storeTokens(data: LoginSuccess): void {
    localStorage.setItem('token', data.token);
  }
}
