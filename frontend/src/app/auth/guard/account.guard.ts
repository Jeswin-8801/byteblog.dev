import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../../service/auth/auth.service';

// prevent authenticated users from accessing authentication related routes such as /login or /register

export const accountGuard: CanActivateFn = (route, state) => {
  if (inject(AuthService).isAuthenticated()) {
    inject(Router).navigate(['/']);
    return false;
  }
  return true;
};
