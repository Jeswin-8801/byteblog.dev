export class AlertModal {
  isWarning?: boolean;
  headerMessage?: string;
  message?: string;

  showButtonCancel?: boolean;
  showButtonPrimary?: boolean;
  buttonPrimaryText?: string;
  primaryButtonRedirectLink?: string;

  set(
    isWarning: boolean,
    headerMessage: string,
    message: string,
    showButtonCancel: boolean,
    showButtonPrimary: boolean,
    buttonPrimaryText: string,
    primaryButtonRedirectLink: string
  ) {
    this.isWarning = isWarning;
    this.headerMessage = headerMessage;
    this.message = message;
    this.showButtonCancel = showButtonCancel;
    this.showButtonPrimary = showButtonPrimary;
    this.buttonPrimaryText = buttonPrimaryText;
    this.primaryButtonRedirectLink = primaryButtonRedirectLink;
  }
}
