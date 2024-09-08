import {
  HttpContextToken,
  HttpInterceptorFn,
  HttpRequest,
} from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../service/auth/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  if (inject(AuthService).isAuthenticated()) {
    const authRequest = addAuthorizationHeader(req);
    return next(authRequest);
  }

  return next(req);
};

const addAuthorizationHeader = (req: HttpRequest<any>) => {
  const token = localStorage.getItem('token');
  return req.clone({
    headers: req.headers.set('Authorization', `Bearer ${token}`),
  });
};

export const IS_PUBLIC = new HttpContextToken(() => false);
