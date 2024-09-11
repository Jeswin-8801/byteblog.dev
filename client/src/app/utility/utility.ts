import { Location } from '@angular/common';
import { HttpParams } from '@angular/common/http';
import { AbstractControl, ValidatorFn } from '@angular/forms';

export class Utility {
  public static matchValidator(
    controlName: string,
    matchingControlName: string
  ): ValidatorFn {
    return (abstractControl: AbstractControl) => {
      const control = abstractControl.get(controlName);
      const matchingControl = abstractControl.get(matchingControlName);

      if (
        matchingControl!.errors &&
        !matchingControl!.errors?.['confirmedValidator']
      ) {
        return null;
      }

      if (control!.value !== matchingControl!.value) {
        const error = { confirmedValidator: 'Passwords do not match.' };
        matchingControl!.setErrors(error);
        return error;
      } else {
        matchingControl!.setErrors(null);
        return null;
      }
    };
  }

  public static getHighlightedText(text: string) {
    return (
      '<span class="max-w-lg text-base font-semibold font-serif leading-normal text-gray-900">' +
      text +
      '</span>'
    );
  }

  public static removeQueryParams(location: Location, paramName: string) {
    const [path, query] = location.path().split('?');
    let params = new HttpParams({ fromString: query });
    params = params.delete(paramName);
    location.replaceState(path, params.toString());
  }
}
