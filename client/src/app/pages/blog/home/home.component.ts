import { Component, inject } from '@angular/core';
import { AuthService } from '../../../service/auth/auth.service';
import { NavbarComponent } from '../../../components/navbar/navbar.component';
import { AlertModal } from '../../../components/alert-modal/alert-modal';
import { ActivatedRoute } from '@angular/router';
import { AlertModalComponent } from '../../../components/alert-modal/alert-modal.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [AlertModalComponent, NavbarComponent],
  templateUrl: './home.component.html',
})
export class HomeComponent {
  private readonly authService = inject(AuthService);
  private readonly route = inject(ActivatedRoute);

  alertModal: AlertModal = new AlertModal();
  isAlertModalClosed: boolean = true;

  ngOnInit() {
    this.setOauthSuccessMessage();
  }

  private setOauthSuccessMessage() {
    this.route.queryParams.subscribe((params) => {
      if (params['registered'] !== undefined) {
        this.toggleAlertModal();

        const user = this.authService.user();
        if (params['registered'] === 'Success')
          this.alertModal.set(
            false,
            'You are good to go!',
            'Successfully linked your ' +
              AlertModalComponent.getHighlightedText(user?.authProvider!) +
              ' account ' +
              AlertModalComponent.getHighlightedText(user?.email!),
            false,
            true,
            'OK',
            ''
          );
      }
    });
  }

  toggleAlertModal() {
    this.isAlertModalClosed = !this.isAlertModalClosed;
  }
}
