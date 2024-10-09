import { Component, DestroyRef, inject } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import {
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

import { Login } from './interfaces/login.interface';
import { AuthService } from '../../service/auth/auth.service';
import { AppConstants } from '../../common/app.constants';
import { LoginError } from './interfaces/login-error.interface';
import { AlertModalComponent } from '../../components/alert-modal/alert-modal.component';
import { AlertModal } from '../../components/alert-modal/alert-modal';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { Utility } from '../../utility/utility';
import { AlertModalService } from '../../components/alert-modal/alert-modal.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    AlertModalComponent,
    NavbarComponent,
  ],
  templateUrl: './login.component.html',
})
export class LoginComponent {
  private readonly authService = inject(AuthService);
  private readonly alertModalService = inject(AlertModalService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly location = inject(Location);

  private readonly EMAIL = 'email';
  private readonly IS_EMAIL_VERIFIED_QUERY_PARAM = 'isEmailVerified';
  private readonly IS_PASSWORD_RESET_QUERY_PARAM = 'isPasswordReset';

  subscription!: Subscription;
  loginForm!: FormGroup;

  alertModal: AlertModal = new AlertModal();
  isAlertModalClosed: boolean = true;

  passwordResetForm!: FormGroup;
  isPasswordResetModalClosed: boolean = true;

  isRedirect!: boolean;
  params: string[] = [];

  showPassword: boolean = false;
  isAlertClosed: boolean = true;
  alertMessage: string = '';

  ngOnInit() {
    this.subscription = this.alertModalService.clickPrimary$.subscribe(
      (isClicked) => {
        if (isClicked && this.alertModal.isPrimaryButtonSubscribedToService) {
          if (this.isRedirect) this.redirectToSignUp();
          else {
            for (const value of this.params)
              Utility.removeQueryParams(this.location, value);
          }
        }
      }
    );

    this.processQueryParams();
    this.createLoginForm();
    this.createPasswordResetForm();
  }

  private processQueryParams() {
    this.processVerifyEndpointRedirects();
    this.processOauthRequestResponse();
    this.setSignUpSuccessMessage();
    this.setSignOutSuccessMessage();
  }

  private processVerifyEndpointRedirects() {
    this.route.queryParams.subscribe((params) => {
      if (params[this.IS_EMAIL_VERIFIED_QUERY_PARAM] as boolean) {
        this.params.push(this.IS_EMAIL_VERIFIED_QUERY_PARAM);
        this.params.push(this.EMAIL);
        this.alertModal.set(
          false,
          'Email Verified',
          'The email ' +
            Utility.getHighlightedText(params[this.EMAIL]) +
            ' has been verified. You can now Sign-in to your account',
          false,
          true,
          'OK',
          true
        );
        if (this.isAlertModalClosed) this.toggleAlertModal();
      } else if (params[this.IS_PASSWORD_RESET_QUERY_PARAM] as boolean) {
        this.params.push(this.IS_PASSWORD_RESET_QUERY_PARAM);
        this.params.push(this.EMAIL);
        this.alertModal.set(
          false,
          'Password has been Reset',
          'The password associated with the email ' +
            Utility.getHighlightedText(params[this.EMAIL]) +
            ' has been reset successfully',
          false,
          true,
          'OK',
          true
        );
        if (this.isAlertModalClosed) this.toggleAlertModal();
      }
    });
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
        this.router.navigateByUrl('/blog/explore');
      } else if (params['error'] !== undefined) {
        console.error(
          'OAuth authentication failed with error: ',
          params['error']
        );
        this.toggleAlert();
        if ((params['error'] as string).endsWith('exists'))
          this.alertMessage = params['error'];
        else this.alertMessage = 'OAuth Authentication Unsuccessfull';
      }
    });
  }

  private setSignOutSuccessMessage() {
    this.route.queryParams.subscribe((params) => {
      if (params['loggedOut'] !== undefined && params['user'] !== undefined) {
        this.toggleAlertModal();
        this.params.push('loggedOut');
        this.params.push('user');

        this.alertModal.set(
          false,
          'Signed out',
          'The account ' +
            Utility.getHighlightedText(params['user']) +
            ' has been successfully signed out',
          false,
          true,
          'OK',
          true
        );
      }
    });
  }

  private setSignUpSuccessMessage() {
    this.route.queryParams.subscribe((params) => {
      if (params['registered'] !== undefined) {
        this.toggleAlertModal();
        this.params.push('registered');

        this.alertModal.set(
          false,
          'Account created',
          'A new account has been created with the username ' +
            Utility.getHighlightedText(params['registered']) +
            '. Please confirm your email to proceed.',
          false,
          true,
          'OK',
          true
        );
      }
    });
  }

  private createLoginForm() {
    this.loginForm = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', Validators.required),
    });
  }

  private createPasswordResetForm() {
    this.passwordResetForm = new FormGroup({
      passwordResetEmail: new FormControl('', [
        Validators.required,
        Validators.email,
      ]),
    });
  }

  onLoginFormSubmitted() {
    // stop here if form is invalid
    if (!this.loginForm.valid) {
      this.email?.markAsTouched();
      this.password?.markAsTouched();
      return;
    }

    this.authService
      .login(this.loginForm.value as Login)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (loginResponse) => {
          console.log('Login Successful: ', loginResponse);
          // get return url from query parameters or default to explore page
          const returnUrl =
            this.route.snapshot.queryParams['returnUrl'] || '/blog/explore';
          this.router.navigateByUrl(returnUrl);
        },
        error: (response) => {
          const errorResponse = response.error as LoginError;
          errorResponse.code = response.status;

          if (this.isAlertClosed) {
            console.error('error occured on login', errorResponse);

            if (errorResponse.code == 404) {
              this.alertModal.set(
                true,
                'Warning',
                errorResponse.message,
                true,
                true,
                'Sign Up',
                true
              );
              this.isRedirect = true;
              if (this.isAlertModalClosed) this.toggleAlertModal();
            } else {
              this.alertMessage = errorResponse.message;
              if (this.isAlertClosed) this.toggleAlert();
            }
          }
        },
      });
  }

  onPasswordResetFormSubmitted() {
    // stop here if form is invalid
    if (!this.passwordResetForm.valid) {
      this.passwordResetEmail?.markAsTouched();
      return;
    }

    this.authService
      .sendPasswordResetMail(this.passwordResetEmail?.value)
      .subscribe();

    // close if open
    if (!this.isPasswordResetModalClosed) this.togglePasswordResetModal();

    this.toggleAlertModal();
    this.alertModal.set(
      false,
      'Password Reset Link Sent',
      'A link to change your password has been sent to your mail ' +
        Utility.getHighlightedText(this.passwordResetEmail?.value),
      false,
      true,
      'OK',
      false
    );
    if (this.isAlertModalClosed) this.toggleAlertModal();
  }

  private redirectToSignUp() {
    this.router.navigateByUrl('/auth/sign-up');
  }

  githubSignInOnClickRedirect() {
    window.open(AppConstants.GITHUB_OAUTH_URL + AppConstants.LOGIN, '_self');
  }

  googleSignInOnClickRedirect() {
    window.open(AppConstants.GOOGLE_OAUTH_URL + AppConstants.LOGIN, '_self');
  }

  toggleAlertModal() {
    this.isAlertModalClosed = !this.isAlertModalClosed;
  }

  togglePasswordResetModal() {
    this.isPasswordResetModalClosed = !this.isPasswordResetModalClosed;
  }

  toggleAlert() {
    this.isAlertClosed = !this.isAlertClosed;
  }

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  get email() {
    return this.loginForm.get('email');
  }

  get password() {
    return this.loginForm.get('password');
  }

  get passwordResetEmail() {
    return this.passwordResetForm.get('passwordResetEmail');
  }

  ngOnDestroy() {
    // prevent memory leak when component destroyed
    this.subscription.unsubscribe();
  }
}
