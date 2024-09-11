import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { AlertModal } from './alert-modal';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { AlertModalService } from './alert-modal.service';

@Component({
  selector: 'app-alert-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './alert-modal.component.html',
})
export class AlertModalComponent {
  private readonly router = inject(Router);
  private readonly alertModalService = inject(AlertModalService);

  @Input() isClosed: boolean = true;
  @Output() isClosedChange = new EventEmitter<boolean>();

  @Input() alertModal!: AlertModal;

  toggleAlert() {
    this.isClosed = !this.isClosed;
    this.isClosedChange.emit(this.isClosed);
  }

  togglePrimaryButton() {
    this.alertModalService.emitValue(true);
    this.toggleAlert();
  }
}
