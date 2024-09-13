import { Component, inject } from '@angular/core';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../service/auth/auth.service';
import { User } from '../../models/user';
import { AccountsComponent } from './accounts/accounts.component';
import { ProfileComponent } from './profile/profile.component';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, NavbarComponent, AccountsComponent, ProfileComponent],
  templateUrl: './settings.component.html',
})
export class SettingsComponent {
  private readonly authService = inject(AuthService);

  selectedSection!: string;
  user!: User;

  ngOnInit() {
    const user = this.authService.user();
    if (user) this.user = user;

    this.onClickAccounts();
  }

  onClickAccounts() {
    this.selectedSection = 'Accounts';
  }

  onClickProfile() {
    this.selectedSection = 'Profile';
  }
}
