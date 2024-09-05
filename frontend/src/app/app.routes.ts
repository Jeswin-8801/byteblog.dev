import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { SignUpComponent } from './auth/sign-up/sign-up.component';
import { HomeComponent } from './home/home.component';
import { accountGuard } from './auth/guard/account.guard';

export const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
  },
  {
    path: 'auth/login',
    component: LoginComponent,
    canActivate: [accountGuard],
  },
  {
    path: 'auth/sign-up',
    component: SignUpComponent,
    canActivate: [accountGuard],
  },
];
