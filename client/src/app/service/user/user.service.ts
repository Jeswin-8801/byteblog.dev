import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';

import { StandardResponseDto } from '../../models/dtos/standard-response-dto';
import { environment } from '../../../environments/environment';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';
import { Utility } from '../../utility/utility';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  constructor() {}

  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);
  private readonly headers = new HttpHeaders().set(
    'content-type',
    'application/json'
  );

  changePassword(body: String): Observable<StandardResponseDto> {
    return this.http
      .post<StandardResponseDto>(
        `${environment.apiUrl}/user/change-password`,
        body,
        { headers: this.headers }
      )
      .pipe(
        tap((data) => {
          const response = data as StandardResponseDto;
          console.log(response.message);
        })
      );
  }

  deleteAccount(id: String): Observable<StandardResponseDto> {
    return this.http
      .delete<StandardResponseDto>(`${environment.apiUrl}/user/delete?id=` + id)
      .pipe(
        tap((data) => {
          const response = data as StandardResponseDto;
          console.log(response.message);
          this.router.navigate([''], {
            queryParams: {
              deleted: this.authService.user()?.email,
            },
          });
        })
      );
  }
}
