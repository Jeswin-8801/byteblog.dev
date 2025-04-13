import { DestroyRef, inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { AuthService } from '../../service/auth/auth.service';
import { AppConstants } from '../../common/app.constants';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { StandardResponseDto } from '../../models/dtos/user/standard-response-dto';

// Users not logged in, cannot access specific pages; Once refresh-token expires, they are redirected to "auth/login"
export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const destroyRef = inject(DestroyRef);

  if (!authService.isRefreshTokenValid()) {
    authService
      .logout(AppConstants.REFRESH_TOKEN_EXPIRY_REDIRECT)
      .pipe(takeUntilDestroyed(destroyRef))
      .subscribe({
        error: (response) => {
          console.log(response as StandardResponseDto);
        },
      });
    return false;
  }

  if (!authService.isAuthenticated()) {
    authService
      .refreshToken()
      .pipe(takeUntilDestroyed(destroyRef))
      .subscribe({
        next: (data) => {
          authService.storeAccessToken(data);
        },
        error: (response) => {
          console.log(response.error as StandardResponseDto);
          return false;
        },
      });
  }

  return true;
};
