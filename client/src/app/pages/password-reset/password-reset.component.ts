import { Component, inject } from '@angular/core';
import { AuthService } from '../../service/auth/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { PasswordResetDto } from '../../models/dtos/password-reset-dto';
import {
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Utility } from '../../utility/utility';
import { AlertModal } from '../../components/alert-modal/alert-modal';
import { AlertModalComponent } from '../../components/alert-modal/alert-modal.component';

@Component({
  selector: 'app-password-reset',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    AlertModalComponent,
  ],
  templateUrl: './password-reset.component.html',
})
export class PasswordResetComponent {
  private readonly authService = inject(AuthService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  private readonly EMAIL = 'email';
  private readonly VERIFICATION_CODE = 'verificationCode';

  passwordResetForm!: FormGroup;
  passwordResetDto!: PasswordResetDto;

  alertModal: AlertModal = new AlertModal();
  isAlertModalClosed: boolean = true;

  ngOnInit() {
    this.getQueryParams();
    this.createPasswordResetForm();
  }

  private getQueryParams() {
    this.route.queryParams.subscribe((params) => {
      this.passwordResetDto = new PasswordResetDto();
      this.passwordResetDto.email = params[this.EMAIL];
      this.passwordResetDto.verificationCode = params[this.VERIFICATION_CODE];
    });
  }

  private createPasswordResetForm() {
    this.passwordResetForm = new FormGroup(
      {
        new_password: new FormControl('', [
          Validators.required,
          Validators.minLength(8),
          Validators.pattern('^(?=.*[a-zA-Z])(?=.*[\\d]).+$'),
        ]),
        confirm_password: new FormControl('', [Validators.required]),
      },
      {
        validators: Utility.matchValidator('new_password', 'confirm_password'),
      }
    );
  }

  onPasswordResetFormSubmit() {
    if (this.passwordResetForm.invalid) {
      this.new_password?.markAllAsTouched();
      this.confirm_password?.markAllAsTouched();
      return;
    }

    this.passwordResetDto.newPassword = this.new_password?.value;

    this.authService.passwordReset(this.passwordResetDto).subscribe({
      next: (responseDto) => {
        console.log('password reset sussceesful: ', responseDto);
        this.router.navigate(['/auth/login'], {
          queryParams: {
            isPasswordReset: true,
            email: this.passwordResetDto.email,
          },
        });
      },
      error: (response) => {
        console.log('password reset unsussceesful: ', response.error);
        this.alertModal.set(
          true,
          'Warning',
          'Password reset link expired',
          false,
          true,
          'OK',
          false
        );
        if (this.isAlertModalClosed) this.toggleAlertModal();
      },
    });
  }

  toggleAlertModal() {
    this.isAlertModalClosed = !this.isAlertModalClosed;
  }

  get new_password() {
    return this.passwordResetForm.get('new_password');
  }

  get confirm_password() {
    return this.passwordResetForm.get('confirm_password');
  }
}
