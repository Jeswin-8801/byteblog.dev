import { Routes } from '@angular/router';

import { LoginComponent } from './auth/login/login.component';
import { accountGuard } from './auth/guard/account.guard';
import { SignupComponent } from './auth/signup/signup.component';
import { ExploreComponent } from './pages/blog/explore/explore.component';
import { PageNotFoundComponent } from './pages/page-not-found/page-not-found.component';
import { LandingComponent } from './pages/landing/landing.component';
import { SettingsComponent } from './pages/settings/settings.component';
import { authGuard } from './auth/guard/auth.guard';
import { VerifyEndpointComponent } from './components/verify-endpoint/verify-endpoint.component';
import { PasswordResetComponent } from './pages/password-reset/password-reset.component';
import { ActivityComponent } from './pages/blog/activity/activity.component';
import { AddBlogComponent } from './pages/blog/add-blog/add-blog.component';
import { BlogComponent } from './pages/blog/blog/blog.component';
import { UserComponent } from './pages/blog/user/user.component';

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
    path: 'auth/verify',
    component: VerifyEndpointComponent,
    canActivate: [accountGuard],
  },
  {
    path: 'auth/password-reset',
    component: PasswordResetComponent,
    canActivate: [accountGuard],
  },
  {
    path: 'blog/explore',
    component: ExploreComponent,
  },
  {
    path: 'blog/activity',
    component: ActivityComponent,
    canActivate: [authGuard],
  },
  {
    path: 'blog/post-blog',
    component: AddBlogComponent,
    canActivate: [authGuard],
  },
  {
    path: 'blog/:headingUri',
    component: BlogComponent,
  },
  {
    path: 'blog/author/:username',
    component: UserComponent,
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
