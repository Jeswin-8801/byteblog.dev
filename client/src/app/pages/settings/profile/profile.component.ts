import { Component, ElementRef, inject, ViewChild } from '@angular/core';
import { AlertModal } from '../../../components/alert-modal/alert-modal';
import { AlertModalComponent } from '../../../components/alert-modal/alert-modal.component';
import { User } from '../../../models/user';
import { CommonModule } from '@angular/common';
import { UserService } from '../../../service/user/user.service';
import { AuthService } from '../../../service/auth/auth.service';
import { ObjectMapper } from 'json-object-mapper';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
} from '@angular/forms';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    AlertModalComponent,
  ],
  templateUrl: './profile.component.html',
})
export class ProfileComponent {
  private readonly authService = inject(AuthService);
  private readonly userService = inject(UserService);

  isDataLoaded = false;

  user!: User;
  userForm!: FormGroup;
  selectedImage: File | undefined;

  alertModal: AlertModal = new AlertModal();
  isAlertModalClosed: boolean = true;
  alertMessage: string = '';
  isAlertClosed: boolean = true;

  @ViewChild('changeAvatarInput')
  changeAvatarElement!: ElementRef;
  userProfilePicUri: string = '/defaults/default-user-profile-pic.png';

  ngOnInit() {
    this.getUserDetails();
  }

  private createForm() {
    this.userForm = new FormBuilder().group({
      status: this.user.isOnline,
      username: this.user.username,
      firstName: this.user.fullName?.match('\\w+ \\w+')
        ? this.user.fullName?.split(' ')[0]
        : this.user.fullName,
      lastName: this.user.fullName?.match('\\w+ \\w+')
        ? this.user.fullName?.split(' ')[1]
        : '',
      about: this.user.about,
    });
  }

  private getUserDetails() {
    this.userService.getUserDetails(this.authService.user()?.id!).subscribe({
      next: (user) => {
        this.user = ObjectMapper.deserialize(User, user);
        this.createForm();
        if (this.user.profileImageUrl)
          this.userProfilePicUri = this.user.profileImageUrl;
        this.isDataLoaded = true;
      },
      error: (response) => {
        console.log('Error on retrieving user details', response.error);
      },
    });
  }

  onImageSelected(event: Event): void {
    const element = event.currentTarget as HTMLInputElement;
    let fileList: FileList | null = element.files;
    if (fileList) this.selectedImage = fileList[0];

    //Show image preview
    const reader = new FileReader();
    reader.onload = () => (this.userProfilePicUri = reader.result as string);
    reader.readAsDataURL(this.selectedImage!);
  }

  onUpdateFormSubmitted() {
    if (!this.userForm.valid) {
      if (this.username?.getError('required')) {
        this.alertMessage = 'Username cannot be empty';
        if (this.isAlertClosed) this.toggleAlert();
      }
      return;
    }

    if (this.firstName?.value && !this.lastName?.value) return;

    if (!this.firstName?.value && this.lastName?.value) return;

    if (
      !this.userProfilePicUri.match(this.selectedImage?.name as string) ||
      this.status?.value !== this.user.isOnline ||
      this.username?.value !== this.user.username ||
      (this.user.fullName &&
        this.firstName?.value + ' ' + this.lastName?.value !==
          this.user.fullName) ||
      this.about?.value ||
      (this.user.about && this.about?.value !== this.user.about)
    ) {
      this.user.isOnline = this.status?.value;
      this.user.username = this.username?.value;
      this.user.fullName = this.firstName?.value + ' ' + this.lastName?.value;
      this.user.about = this.about?.value;
      this.userService.updateUser(this.user, this.selectedImage!).subscribe({
        next: (response) => {
          console.log('User update returned response:', response);
          this.alertModal.set(
            false,
            'Success',
            'Successfully updated user profile details',
            false,
            true,
            'OK',
            false
          );
          if (this.isAlertModalClosed) this.toggleAlertModal();
          this.authService.refreshToken().subscribe({
            next: (tokenDto) => {
              this.authService.storeAccessToken(tokenDto);
            },
          });
        },
        error: (response) => {
          console.log('Error on user update:', response.error);
          if (response.status === 409) {
            this.alertMessage = 'Username already in use';
            if (this.isAlertClosed) this.toggleAlert();
          } else if (response.message === 'Maximum upload size exceeded') {
            this.alertModal.set(
              true,
              'Warning',
              response.message,
              false,
              true,
              'OK',
              false
            );
            if (this.isAlertModalClosed) this.toggleAlertModal();
          }
        },
      });
    } else {
      this.alertModal.set(
        true,
        'Warning',
        'Nothing to update',
        false,
        true,
        'OK',
        false
      );
      if (this.isAlertModalClosed) this.toggleAlertModal();
    }
  }

  onClickDeletePicture() {
    this.userProfilePicUri = '/defaults/default-user-profile-pic.png';
    this.changeAvatarElement.nativeElement.value = '';
    this.selectedImage = undefined;
  }

  toggleAlert() {
    this.isAlertClosed = !this.isAlertClosed;
  }

  toggleAlertModal() {
    this.isAlertModalClosed = !this.isAlertModalClosed;
  }

  get status() {
    return this.userForm.get('status');
  }

  get username() {
    return this.userForm.get('username');
  }

  get firstName() {
    return this.userForm.get('firstName');
  }

  get lastName() {
    return this.userForm.get('lastName');
  }

  get about() {
    return this.userForm.get('about');
  }
}
