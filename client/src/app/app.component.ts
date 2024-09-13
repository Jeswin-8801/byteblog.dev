import { Component, HostListener, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { UtilitiesService } from './service/utilities/utilities.service';
import { Subject, Subscription, takeUntil } from 'rxjs';
import { AlertModalComponent } from './components/alert-modal/alert-modal.component';
import { ErrorService } from './service/error/error.service';
import { AlertModal } from './components/alert-modal/alert-modal';
import { AlertModalService } from './components/alert-modal/alert-modal.service';
import { AuthService } from './service/auth/auth.service';
import { AppConstants } from './common/app.constants';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, AlertModalComponent],
  templateUrl: './app.component.html',
})
export class AppComponent {
  private readonly utilitiesService = inject(UtilitiesService);
  private readonly errorService = inject(ErrorService);
  private readonly alertModalService = inject(AlertModalService);
  private readonly authService = inject(AuthService);
  subscription!: Subscription;
  private ngUnsubscribe = new Subject();

  alertModal!: AlertModal;
  isAlertModalClosed: boolean = true;

  hasError: boolean = false;

  title = 'client';

  ngOnInit() {
    this.initializeErrors();
    this.subscription = this.alertModalService.clickPrimary$.subscribe(
      (isClicked) => {
        if (isClicked && this.hasError) {
          this.authService
            .logout(AppConstants.REFRESH_TOKEN_EXPIRY_REDIRECT)
            .subscribe();
        }
      }
    );
  }

  private initializeErrors() {
    this.errorService
      .getErrors()
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((error) => {
        this.alertModal = error;
        this.hasError = true;
        this.toggleAlertModal();
      });
  }

  @HostListener('document:click', ['$event'])
  documentClick(event: any): void {
    this.utilitiesService.documentClickedTarget.next(event.target);
  }

  toggleAlertModal() {
    this.isAlertModalClosed = !this.isAlertModalClosed;
  }

  ngOnDestroy() {
    this.ngUnsubscribe.complete();
    this.subscription.unsubscribe();
  }
}
