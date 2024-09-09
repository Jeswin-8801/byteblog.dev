import { CommonModule } from '@angular/common';
import { Component, inject, Input } from '@angular/core';
import { AlertModal } from './alert-modal';
import { Router } from '@angular/router';

@Component({
  selector: 'app-alert-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './alert-modal.component.html',
})
export class AlertModalComponent {
  private readonly router = inject(Router);

  @Input() isClosed: boolean = true;
  @Input() alertModal!: AlertModal;

  toggleAlert() {
    this.isClosed = !this.isClosed;
  }

  buttonPrimaryRedirect() {
    if (this.alertModal.primaryButtonRedirectLink!.length > 0)
      this.router.navigateByUrl(this.alertModal.primaryButtonRedirectLink!);
    else this.toggleAlert();
  }

  public static getHighlightedText(text: string) {
    return (
      '<span class="max-w-lg text-base font-semibold font-serif leading-normal text-gray-900">' +
      text +
      '</span>'
    );
  }
}
