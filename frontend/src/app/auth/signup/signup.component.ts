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
import { ActivatedRoute, Router } from '@angular/router';

import { SignUp } from './interfaces/sign-up.interface';
import { AuthService } from '../../service/auth/auth.service';
import { AppConstants } from '../../common/app.constants';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { Utility } from '../../utility/utility';
import { ObjectMapper } from 'json-object-mapper';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [FormsModule, ReactiveFormsModule, CommonModule, NavbarComponent],
  templateUrl: './signup.component.html',
})
export class SignupComponent {
  private readonly authService = inject(AuthService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  signUpForm!: FormGroup;

  showPassword: boolean = false;
  isAlertClosed: boolean = true;
  alertMessage: string = '';

  ngOnInit() {
    this.processOauthRequestResponse();
    this.createForm();
  }

  private processOauthRequestResponse() {
    this.route.queryParams.subscribe((params) => {
      if (
        params[AppConstants.ACCESS_TOKEN] !== undefined &&
        params[AppConstants.REFRESH_TOKEN] !== undefined
      ) {
        this.authService.storeTokens(
          AppConstants.ACCESS_TOKEN,
          params[AppConstants.ACCESS_TOKEN]
        );
        this.authService.storeTokens(
          AppConstants.REFRESH_TOKEN,
          params[AppConstants.REFRESH_TOKEN]
        );
        this.router.navigateByUrl('blog/explore');

        const user = this.authService.user();
        this.router.navigate(['blog/explore'], {
          queryParams: {
            registered: 'Success',
          },
        });
      } else if (params['error'] !== undefined) {
        console.error(
          'OAuth authentication failed with error: ' + params['error']
        );
        this.toggleAlert();
        this.alertMessage = 'OAuth Authentication Unsuccessfull';
      }
    });
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
        validators: Utility.matchValidator('password', 'password_confirm'),
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
      .signup(ObjectMapper.deserialize(SignUp, this.signUpForm.value))
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.router.navigate(['auth/login'], {
            queryParams: { registered: this.username?.value },
          });
        },
        error: (response) => {
          this.alertMessage = response.error.message;
          if (this.isAlertClosed) this.toggleAlert();
        },
      });
  }

  githubSignUpOnClickRedirect() {
    window.open(AppConstants.GITHUB_OAUTH_URL + AppConstants.SIGNUP, '_self');
  }

  googleSignUpOnClickRedirect() {
    window.open(AppConstants.GOOGLE_OAUTH_URL + AppConstants.SIGNUP, '_self');
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
