import {
  Component,
  DestroyRef,
  EventEmitter,
  inject,
  Input,
  Output,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';

import { AlertModal } from '../../../components/alert-modal/alert-modal';
import { Utility } from '../../../utility/utility';
import { AlertModalComponent } from '../../../components/alert-modal/alert-modal.component';
import { UserService } from '../../../service/user/user.service';
import { ObjectMapper } from 'json-object-mapper';
import { ChangePasswordDto } from '../../../models/dtos/change-password-dto';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { StandardResponseDto } from '../../../models/dtos/standard-response-dto';
import { AlertModalService } from '../../../components/alert-modal/alert-modal.service';
import { Subscription } from 'rxjs';
import { TokenClaimsUserDto } from '../../../models/dtos/token-claims-user-dto';

@Component({
  selector: 'app-accounts',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    AlertModalComponent,
  ],
  templateUrl: './accounts.component.html',
})
export class AccountsComponent {
  private readonly userService = inject(UserService);
  private readonly alertModalService = inject(AlertModalService);
  private readonly destroyRef = inject(DestroyRef);
  subscription!: Subscription;

  changePasswordForm!: FormGroup;
  showPassword: boolean = false;

  @Input({ required: true }) user!: TokenClaimsUserDto;

  alertModal: AlertModal = new AlertModal();
  isAlertModalClosed: boolean = true;

  isAlertClosed: boolean = true;
  alertMessage: string = '';

  ngOnInit() {
    this.subscription = this.alertModalService.clickPrimary$.subscribe(
      (isClicked) => {
        if (isClicked && this.alertModal.isPrimaryButtonSubscribedToService)
          this.onClickPrimaryButtonDeleteAccount();
      }
    );
    this.createForm();
  }

  private createForm() {
    this.changePasswordForm = new FormGroup({
      current_password: new FormControl('', Validators.required),
      new_password: new FormControl('', [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern('^(?=.*[a-zA-Z])(?=.*[\\d]).+$'),
      ]),
    });
  }

  private onClickPrimaryButtonDeleteAccount() {
    if (this.user.id) {
      this.userService
        .deleteAccount(this.user.id)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          error: (response) => {
            const errorResponse = response.error as StandardResponseDto;
            errorResponse.code = response.status;

            if (this.isAlertModalClosed) {
              console.error('error occured on account deletion', errorResponse);
              if (errorResponse.message) {
                this.alertModal.set(
                  true,
                  'Error',
                  'An error occured on account deletion',
                  false,
                  true,
                  'Close',
                  true
                );
                this.toggleAlertModal();
              }
            }
          },
        });
    } else {
      this.alertModal.set(
        true,
        'Error',
        'An error occured on account deletion. ' +
          Utility.getHighlightedText('User ID could not be found'),
        false,
        true,
        'Close',
        false
      );
      if (this.isAlertModalClosed) this.toggleAlertModal();
    }
  }

  onChangePasswordFormSubmitted() {
    if (this.user.authProvider !== 'local') {
      // sets showAlertModal = true in parent component settings
      if (this.isAlertModalClosed) this.toggleAlertModal();

      this.alertModal.set(
        true,
        'Warning',
        'Change of Password not allowed for accounts registered with external Auth providers (' +
          Utility.getHighlightedText('Google') +
          ', ' +
          Utility.getHighlightedText('Github') +
          ')',
        false,
        true,
        'OK',
        false
      );
      return;
    }

    // stop here if form is invalid
    if (!this.changePasswordForm.valid) {
      this.current_password?.markAsTouched();
      this.new_password?.markAsTouched();

      if (
        this.current_password?.getError('required') ||
        this.new_password?.getError('required')
      )
        this.alertMessage = 'Password fields cannot be empty';
      else if (this.new_password?.getError('minlength'))
        this.alertMessage = 'New password must be atleast 8 characters long';
      else if (this.new_password?.getError('pattern'))
        this.alertMessage =
          'New password must have atleast 1 digit and 1 alphabet';

      if (this.isAlertClosed) this.toggleAlert();
      return;
    }

    // check for if passwords match
    if (this.new_password?.value === this.current_password?.value) {
      this.alertMessage =
        'New password must be different from currect password';

      if (this.isAlertClosed) this.toggleAlert();
      return;
    }

    let changePasswordDto = new ChangePasswordDto();
    changePasswordDto.id = this.user.id;
    changePasswordDto.currentPassword = this.current_password?.value;
    changePasswordDto.newPassword = this.new_password?.value;

    this.userService
      .changePassword(ObjectMapper.serialize(changePasswordDto))
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response) => {
          if (this.isAlertModalClosed) this.toggleAlertModal();
          this.alertModal.set(
            false,
            'Success',
            response.message!,
            false,
            true,
            'OK',
            false
          );
        },
        error: (response) => {
          const errorResponse = response.error as StandardResponseDto;
          errorResponse.code = response.status;

          if (this.isAlertClosed) {
            console.error(
              'error occured on change password request',
              errorResponse
            );
            if (errorResponse.message) {
              this.alertMessage = errorResponse.message;
              this.toggleAlert();
            }
          }
        },
      });
  }

  onClickConfirmDelete() {
    if (this.isAlertModalClosed) this.toggleAlertModal();
    this.alertModal.set(
      true,
      'Confirm Deletion',
      'Are you sure you want to delete the account ' +
        Utility.getHighlightedText(this.user.email!),
      true,
      true,
      'Confirm',
      true
    );
  }

  toggleAlert() {
    this.isAlertClosed = !this.isAlertClosed;
  }

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  toggleAlertModal() {
    this.isAlertModalClosed = !this.isAlertModalClosed;
  }

  get current_password() {
    return this.changePasswordForm.get('current_password');
  }

  get new_password() {
    return this.changePasswordForm.get('new_password');
  }

  ngOnDestroy() {
    // prevent memory leak when component destroyed
    this.subscription.unsubscribe();
  }
}
