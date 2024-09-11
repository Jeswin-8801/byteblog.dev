import { Routes } from '@angular/router';

import { LoginComponent } from './auth/login/login.component';
import { accountGuard } from './auth/guard/account.guard';
import { SignupComponent } from './auth/signup/signup.component';
import { HomeComponent } from './pages/blog/home/home.component';
import { PageNotFoundComponent } from './pages/page-not-found/page-not-found.component';
import { LandingComponent } from './pages/landing/landing.component';
import { SettingsComponent } from './pages/settings/settings.component';
import { authGuard } from './auth/guard/auth.guard';

export const routes: Routes = [
  {
    path: '',
    component: LandingComponent,
  },
  {
    path: 'auth/login',
    component: LoginComponent,
    canActivate: [accountGuard],
  },
  {
    path: 'auth/sign-up',
    component: SignupComponent,
    canActivate: [accountGuard],
  },
  {
    path: 'home',
    component: HomeComponent,
  },
  {
    path: 'settings',
    component: SettingsComponent,
    canActivate: [authGuard],
  },
  {
    path: '**',
    pathMatch: 'full',
    component: PageNotFoundComponent,
  },
];
