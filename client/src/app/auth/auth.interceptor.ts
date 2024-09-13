import {
  HttpContextToken,
  HttpErrorResponse,
  HttpEvent,
  HttpInterceptorFn,
  HttpRequest,
} from '@angular/common/http';
import { DestroyRef, inject } from '@angular/core';
import { AuthService } from '../service/auth/auth.service';
import { catchError, from, NEVER, Observable, switchMap, timeout } from 'rxjs';
import { ErrorService } from '../service/error/error.service';
import { AlertModal } from '../components/alert-modal/alert-modal';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const errorService = inject(ErrorService);

  if (authService.isAuthenticated())
    return next(addAuthorizationHeader(req, authService.getAccessToken()!));
  else if (
    authService.isRefreshTokenValid() &&
    !authService.isAuthenticated()
  ) {
    return from(authService.refreshToken()).pipe(
      switchMap((data) => {
        authService.storeRefreshedToken(data);
        return next(addAuthorizationHeader(req, authService.getAccessToken()!));
      })
    );
  }

  return next(req).pipe(
    timeout(2000),
    catchError((error: HttpErrorResponse) => {
      if (error.status == 401) {
        if (!authService.isRefreshTokenValid()) {
          let alertModal = new AlertModal();
          alertModal.set(
            true,
            'Login to continue',
            'Session expired',
            false,
            true,
            'OK',
            true
          );
          errorService.addErrors(alertModal);
        }
      }
      return NEVER;
    })
  ) as Observable<HttpEvent<unknown>>;
};

const addAuthorizationHeader = (req: HttpRequest<any>, token: string) => {
  return req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
};

export const IS_PUBLIC = new HttpContextToken(() => false);
