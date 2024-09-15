import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';

import { StandardResponseDto } from '../../models/dtos/standard-response-dto';
import { environment } from '../../../environments/environment';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';
import { User } from '../../models/user';
import { ObjectMapper } from 'json-object-mapper';

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

  getUserDetails(id: string): Observable<any> {
    return this.http.get<any>(`${environment.apiUrl}/user?id=` + id).pipe(
      tap(() => {
        console.log('successfully retrieved user details');
      })
    );
  }

  updateUser(user: User, image: File): Observable<StandardResponseDto> {
    const formData: FormData = new FormData();
    formData.append(
      'user',
      new Blob([ObjectMapper.serialize(user) as string], {
        type: 'application/json',
      })
    );
    formData.append('image', image);
    return this.http.put<StandardResponseDto>(
      `${environment.apiUrl}/user`,
      formData
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
}
