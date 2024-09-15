import { Component, inject } from '@angular/core';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../service/auth/auth.service';
import { AccountsComponent } from './accounts/accounts.component';
import { ProfileComponent } from './profile/profile.component';
import { TokenClaimsUserDto } from '../../models/dtos/token-claims-user-dto';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, NavbarComponent, AccountsComponent, ProfileComponent],
  templateUrl: './settings.component.html',
})
export class SettingsComponent {
  private readonly authService = inject(AuthService);

  isUserProfileDataLoaded!: boolean;

  selectedSection!: string;
  user!: TokenClaimsUserDto;

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
