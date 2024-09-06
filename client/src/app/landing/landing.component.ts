import { Component, inject } from '@angular/core';
import { AuthService } from '../service/auth/auth.service';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [],
  templateUrl: './landing.component.html',
})
export class LandingComponent {
  private readonly authService = inject(AuthService);

  isLoggedIn: boolean = false;

  ngOnInit() {
    this.isLoggedIn = this.authService.isAuthenticated();
  }

  logout() {
    this.authService.logout();
  }
}
