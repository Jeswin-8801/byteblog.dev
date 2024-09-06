import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';

import { SignUp } from './interfaces/sign-up.interface';
import { AuthService } from '../../service/auth/auth.service';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [FormsModule, ReactiveFormsModule, CommonModule],
  templateUrl: './signup.component.html',
})
export class SignupComponent {
  private readonly authService = inject(AuthService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly router = inject(Router);
  signUpForm!: FormGroup;

  showPassword: boolean = false;
  isAlertClosed: boolean = true;
  alertMessage: string = '';

  ngOnInit() {
    this.createForm();
  }

  private createForm() {
    this.signUpForm = new FormGroup(
      {
        username: new FormControl('', Validators.required),
        email: new FormControl('', [Validators.required, Validators.email]),
        password: new FormControl('', [
          Validators.required,
          Validators.minLength(8),
          Validators.pattern('^(?=.*[a-zA-Z])(?=.*[\\d]).+$'),
        ]),
        password_confirm: new FormControl('', [Validators.required]),
      },
      {
        validators: this.matchValidator('password', 'password_confirm'),
      }
    );
  }

  onSignUpFormSubmitted() {
    // stop here if form is invalid
    if (!this.signUpForm.valid) {
      this.username?.markAsTouched();
      this.email?.markAsTouched();
      this.password?.markAsTouched();
      this.password_confirm?.markAsTouched();
      return;
    }

    this.authService
      .signup(this.signUpForm.value as SignUp)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (signUpResponse) => {
          console.log(signUpResponse);
          this.router.navigate(['/auth/login'], {
            queryParams: { registered: this.username?.value },
          });
        },
        error: (response) => {
          this.alertMessage = response.error.message;
          if (this.isAlertClosed) this.toggleAlert();
        },
      });
  }

  matchValidator(
    controlName: string,
    matchingControlName: string
  ): ValidatorFn {
    return (abstractControl: AbstractControl) => {
      const control = abstractControl.get(controlName);
      const matchingControl = abstractControl.get(matchingControlName);

      if (
        matchingControl!.errors &&
        !matchingControl!.errors?.['confirmedValidator']
      ) {
        return null;
      }

      if (control!.value !== matchingControl!.value) {
        const error = { confirmedValidator: 'Passwords do not match.' };
        matchingControl!.setErrors(error);
        return error;
      } else {
        matchingControl!.setErrors(null);
        return null;
      }
    };
  }

  toggleAlert() {
    this.isAlertClosed = !this.isAlertClosed;
  }

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  get username() {
    return this.signUpForm.get('username');
  }

  get email() {
    return this.signUpForm.get('email');
  }

  get password() {
    return this.signUpForm.get('password');
  }

  get password_confirm() {
    return this.signUpForm.get('password_confirm');
  }
}