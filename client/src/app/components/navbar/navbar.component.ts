import {
  Component,
  DestroyRef,
  ElementRef,
  inject,
  Input,
  ViewChild,
} from '@angular/core';
import { AuthService } from '../../service/auth/auth.service';
import { CommonModule } from '@angular/common';
import { UtilitiesService } from '../../service/utilities/utilities.service';
import { AppConstants } from '../../common/app.constants';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { StandardResponseDto } from '../../models/dtos/user/standard-response-dto';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navbar.component.html',
})
export class NavbarComponent {
  private readonly authService = inject(AuthService);
  private readonly utilitiesService = inject(UtilitiesService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly router = inject(Router);

  @ViewChild('userIconButton') userIconButton!: ElementRef;

  @Input() isAuthPage: boolean = false;
  @Input() isNavbarFixed: boolean = true;
  @Input() isThemeDark: boolean = true;
  @Input() currentPage: string = '';
  isLoggedIn: boolean = false;
  isOnline!: boolean;
  showUserIconDropdown: boolean = false;
  showMobileMenu: boolean = false;
  userProfilePicUri: string = '/defaults/default-user-profile-pic.png';
  userFullName!: string;
  userEmail!: string;

  ngOnInit() {
    this.setUserDetails();
    this.setDocumentClickListner();
    this.isLoggedIn = this.authService.isRefreshTokenValid();
    this.isOnline = this.authService.user()?.isOnline as boolean;
  }

  private setUserDetails() {
    const user = this.authService.user();
    if (user?.profileImageUrl) this.userProfilePicUri = user.profileImageUrl;
    if (user?.fullName) this.userFullName = user.fullName;
    if (user?.email) this.userEmail = user?.email;
  }

  private setDocumentClickListner() {
    this.utilitiesService.documentClickedTarget.subscribe((target) =>
      this.documentClickListener(target)
    );
  }

  private documentClickListener(target: any): void {
    if (
      this.userIconButton &&
      !this.userIconButton.nativeElement.contains(target) &&
      this.showUserIconDropdown
    )
      this.toggleUserIconDropdown();
  }

  toggleUserIconDropdown() {
    this.showUserIconDropdown = !this.showUserIconDropdown;
  }

  toggleMobileMenu() {
    this.showMobileMenu = !this.showMobileMenu;
  }

  redirectToUserPage() {
    let username = this.authService.user()?.username;
    if (username) this.router.navigateByUrl(`/blog/author/` + username);
  }

  logout() {
    this.authService
      .logout(AppConstants.LOGOUT)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        error: (response) => {
          console.log(response as StandardResponseDto);
        },
      });
  }
}
