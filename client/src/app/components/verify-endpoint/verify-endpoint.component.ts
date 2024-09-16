import { Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { AuthService } from '../../service/auth/auth.service';
import { VerifyEmailDto } from '../../models/dtos/verify-email-dto';
import { ObjectMapper } from 'json-object-mapper';

@Component({
  selector: 'app-verify-endpoint',
  standalone: true,
  imports: [],
  templateUrl: './verify-endpoint.component.html',
})
export class VerifyEndpointComponent {
  private readonly authService = inject(AuthService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  private readonly EMAIL = 'email';
  private readonly VERIFICATION_CODE = 'verificationCode';
  private readonly IS_PASSWORD_RESET = 'isProcessPasswordReset';
  private readonly IS_VERIFY_EMAIL = 'isProcessVerifyEmail';

  ngOnInit() {
    this.route.queryParams.subscribe((params) => {
      if (params[this.IS_VERIFY_EMAIL] as boolean) {
        let verifyEmailDto = new VerifyEmailDto();
        verifyEmailDto.email = params[this.EMAIL];
        verifyEmailDto.verificationCode = params[this.VERIFICATION_CODE];

        this.authService.verifyEmail(verifyEmailDto).subscribe({
          next: (responseDto) => {
            console.log('Email verification successful: ', responseDto);
            this.router.navigate(['auth/login'], {
              queryParams: {
                isEmailVerified: true,
                email: params[this.EMAIL],
              },
            });
          },
          error: (response) => {
            console.log('Email verification unsuccessful: ', response.error);
          },
        });
      } else if (params[this.IS_PASSWORD_RESET] as boolean)
        this.router.navigate(['auth/password-reset'], {
          queryParams: params,
        });
    });
  }
}
