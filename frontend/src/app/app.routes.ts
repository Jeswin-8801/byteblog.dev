import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { SignUpComponent } from './auth/sign-up/sign-up.component';
import { HomeComponent } from './blog/home/home.component';
import { accountGuard } from './auth/guard/account.guard';
import { LandingComponent } from './landing/landing.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';

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
    component: SignUpComponent,
    canActivate: [accountGuard],
  },
  {
    path: 'home',
    component: HomeComponent,
  },
  {
    path: '**',
    pathMatch: 'full',
    component: PageNotFoundComponent,
  },
];
