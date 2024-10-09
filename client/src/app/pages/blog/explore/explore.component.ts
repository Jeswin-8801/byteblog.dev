import { Component, inject } from '@angular/core';
import { AuthService } from '../../../service/auth/auth.service';
import { NavbarComponent } from '../../../components/navbar/navbar.component';
import { AlertModal } from '../../../components/alert-modal/alert-modal';
import { ActivatedRoute, Params } from '@angular/router';
import { AlertModalComponent } from '../../../components/alert-modal/alert-modal.component';
import { Utility } from '../../../utility/utility';
import { Location } from '@angular/common';
import { AlertModalService } from '../../../components/alert-modal/alert-modal.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-explore',
  standalone: true,
  imports: [AlertModalComponent, NavbarComponent],
  templateUrl: './explore.component.html',
})
export class ExploreComponent {
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
      if (params['registered']) {
        this.setOauthSuccessMessage(params);
        this.param = 'registered';
      }
    });
  }

  private setOauthSuccessMessage(params: Params) {
    this.toggleAlertModal();

    const user = this.authService.user();
    if (params['registered'] === 'Success')
      this.alertModal.set(
        false,
        'You are good to go!',
        'Successfully linked your ' +
          Utility.getHighlightedText(user?.authProvider!) +
          ' account ' +
          Utility.getHighlightedText(user?.email!),
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
