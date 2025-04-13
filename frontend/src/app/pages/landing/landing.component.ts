import { Component, inject } from '@angular/core';
import { NavbarComponent } from '../../components/navbar/navbar.component';

import { Location } from '@angular/common';
import { Subscription } from 'rxjs';
import { AlertModalService } from '../../components/alert-modal/alert-modal.service';
import { AlertModal } from '../../components/alert-modal/alert-modal';
import { Utility } from '../../utility/utility';
import { AuthService } from '../../service/auth/auth.service';
import { ActivatedRoute } from '@angular/router';
import { AlertModalComponent } from '../../components/alert-modal/alert-modal.component';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [NavbarComponent, AlertModalComponent],
  templateUrl: './landing.component.html',
})
export class LandingComponent {
  private readonly authService = inject(AuthService);
  private readonly route = inject(ActivatedRoute);
  private readonly location = inject(Location);
  private readonly alertModalService = inject(AlertModalService);
  subscription!: Subscription;

  alertModal: AlertModal = new AlertModal();
  isAlertModalClosed: boolean = true;

  param!: string;

  ngOnInit() {
    this.subscription = this.alertModalService.clickPrimary$.subscribe(
      (isClicked) => {
        if (isClicked && this.alertModal.isPrimaryButtonSubscribedToService)
          Utility.removeQueryParams(this.location, this.param);
      }
    );
    this.handleQueryParams();
  }

  private handleQueryParams() {
    this.route.queryParams.subscribe((params) => {
      if (params['deleted']) {
        this.setDeleteSuccessMessage(params['deleted']);
        this.param = 'deleted';
        this.authService.removeTokens();
      }
    });
  }

  private setDeleteSuccessMessage(email: string) {
    this.toggleAlertModal();

    this.alertModal.set(
      false,
      'Success',
      'Successfully deleted account ' + Utility.getHighlightedText(email),
      false,
      true,
      'OK',
      true
    );
  }

  toggleAlertModal() {
    this.isAlertModalClosed = !this.isAlertModalClosed;
  }

  ngOnDestroy() {
    // prevent memory leak when component destroyed
    this.subscription.unsubscribe();
  }
}
