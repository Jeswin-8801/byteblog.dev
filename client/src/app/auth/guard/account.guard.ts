import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { AuthService } from '../../service/auth/auth.service';
import { AppConstants } from '../../common/app.constants';

// Users already logged in, when trying to access Login or SignUp is redirected to "/"
export const accountGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  if (authService.isAuthenticated()) {
    inject(Router).navigate(['/']);
    return false;
  }
  return true;
};
