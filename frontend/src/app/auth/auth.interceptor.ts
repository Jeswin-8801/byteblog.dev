import {
  HttpContextToken,
  HttpInterceptorFn,
  HttpRequest,
} from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../service/auth/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // if (req.context.get(IS_PUBLIC)) {
  //   return next(req);
  // }

  if (inject(AuthService).isAuthenticated()) {
    console.log(req);
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

// https://medium.com/@naspy971/authentication-system-using-jwt-and-localstorage-in-angular-17-0da056f98610
// https://www.syncfusion.com/blogs/post/best-practices-for-jwt-authentication-in-angular-apps
// https://medium.com/@vergarawalterdomenico/secure-api-calls-in-angular-and-react-with-jwt-token-interceptor-a-ready-to-use-example-790dd059f338
// https://jasonwatmore.com/post/2022/11/29/angular-14-user-registration-and-login-example-tutorial#account-service-ts
