import { Component, DestroyRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
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

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, ReactiveFormsModule, CommonModule],
  templateUrl: './login.component.html',
})
export class LoginComponent {
  private readonly authService = inject(AuthService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  loginForm!: FormGroup;

  showPassword: boolean = false;
  isAlertClosed: boolean = true;
  alertMessage: string = '';
  isInfoMessageClosed: boolean = true;
  infoMessageUsername: string = '';

  ngOnInit() {
    this.processOauthRequestResponse();
    this.setSignUpSuccessMessage();
    this.createForm();
  }

  private processOauthRequestResponse() {
    this.route.queryParams.subscribe((params) => {
      if (params['token'] !== undefined) {
        this.authService.storeTokens(params['token']);
        this.router.navigateByUrl('/home');
      } else if (params['error'] !== undefined) {
        console.error(
          'OAuth authentication failed with error: ' + params['error']
        );
        this.toggleAlert();
        this.alertMessage = 'OAuth Authentication Unsuccessfull';
      }
    });
  }

  private setSignUpSuccessMessage() {
    this.route.queryParams.subscribe((params) => {
      if (params['registered'] !== undefined) {
        this.infoMessageUsername = params['registered'];
        this.toggleInfoMessage();
      }
    });
  }

  private createForm() {
    this.loginForm = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', Validators.required),
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
          console.log(loginResponse);
          // get return url from query parameters or default to home page
          const returnUrl =
            this.route.snapshot.queryParams['returnUrl'] || '/home';
          this.router.navigateByUrl(returnUrl);
        },
        error: (response) => {
          if (response.status == 400 && this.isAlertClosed) {
            this.toggleAlert();
            this.alertMessage = 'Incorrect email or password provided';
          }
        },
      });
  }

  githubSignInOnClickRedirect() {
    window.open(AppConstants.GITHUB_OAUTH_URL + AppConstants.LOGIN, '_self');
  }

  googleSignInOnClickRedirect() {
    window.open(AppConstants.GOOGLE_OAUTH_URL + AppConstants.LOGIN, '_self');
  }

  toggleInfoMessage() {
    this.isInfoMessageClosed = !this.isInfoMessageClosed;
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
}
