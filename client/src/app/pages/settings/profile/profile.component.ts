import { Component } from '@angular/core';
import { AlertModal } from '../../../components/alert-modal/alert-modal';
import { AlertModalComponent } from '../../../components/alert-modal/alert-modal.component';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [AlertModalComponent],
  templateUrl: './profile.component.html',
})
export class ProfileComponent {
  alertModal: AlertModal = new AlertModal();
  isAlertModalClosed: boolean = true;
}
