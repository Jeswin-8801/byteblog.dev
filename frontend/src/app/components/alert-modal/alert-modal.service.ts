import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AlertModalService {
  constructor() {}

  // Objservable boolean source
  private clickPrimarySource = new Subject<boolean>();

  // Observable boolean stream
  clickPrimary$ = this.clickPrimarySource.asObservable();

  // Service message commands
  emitValue(isClicked: boolean) {
    this.clickPrimarySource.next(isClicked);
  }
}
