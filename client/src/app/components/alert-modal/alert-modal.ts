export class AlertModal {
  isWarning?: boolean;
  headerMessage?: string;
  message?: string;

  showButtonCancel?: boolean;
  showButtonPrimary?: boolean;
  buttonPrimaryText?: string;

  isPrimaryButtonSubscribedToService?: boolean;

  set(
    isWarning: boolean,
    headerMessage: string,
    message: string,
    showButtonCancel: boolean,
    showButtonPrimary: boolean,
    buttonPrimaryText: string,
    isPrimaryButtonSubscribedToService: boolean
  ) {
    this.isWarning = isWarning;
    this.headerMessage = headerMessage;
    this.message = message;
    this.showButtonCancel = showButtonCancel;
    this.showButtonPrimary = showButtonPrimary;
    this.buttonPrimaryText = buttonPrimaryText;
    this.isPrimaryButtonSubscribedToService =
      isPrimaryButtonSubscribedToService;
  }
}
