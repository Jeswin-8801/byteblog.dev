import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';

import { Login } from './interfaces/login.interface';
import { AuthService } from '../../service/auth/auth.service';
import { AlertService } from '../../service/alert/alert.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, ReactiveFormsModule, CommonModule],
  templateUrl: './login.component.html',
})
export class LoginComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly alertService = inject(AlertService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  loginForm!: FormGroup;

  showPassword: boolean = false;

  ngOnInit() {
    this.createForm();
  }

  private createForm() {
    this.loginForm = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern('^(?=.*[a-zA-Z])(?=.*[\\d]).+$'),
      ]),
    });
  }

  onLoginFormSubmitted() {
    // reset alerts on submit
    this.alertService.clear();

    // stop here if form is invalid
    if (!this.loginForm.valid) {
      return;
    }

    this.authService
      .login(this.loginForm.value as Login)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          // get return url from query parameters or default to home page
          const returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
          this.router.navigateByUrl(returnUrl);
        },
        error: (error) => {
          this.alertService.error(error);
          console.log(error);
        },
      });
  }

  togglePasswordVisibility() {
    console.log(
      this.password?.errors,
      ', minlength: ',
      this.password?.hasError('minlength'),
      ', pattern: ',
      this.password?.errors?.['pattern']
    );
    this.showPassword = !this.showPassword;
  }

  get email() {
    return this.loginForm.get('email');
  }

  get password() {
    return this.loginForm.get('password');
  }
}
