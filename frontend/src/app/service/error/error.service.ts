import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { AlertModal } from '../../components/alert-modal/alert-modal';

@Injectable({
  providedIn: 'root',
})
export class ErrorService {
  private errors = new Subject<AlertModal>();

  constructor() {}

  public addErrors = (error: AlertModal): void => this.errors.next(error);

  public getErrors = () => this.errors.asObservable();
}
