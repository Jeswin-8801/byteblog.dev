import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { StandardResponseDto } from '../../models/dtos/user/standard-response-dto';
import { PostCommentDto } from '../../models/dtos/comment/post-comment-dto';

@Injectable({
  providedIn: 'root',
})
export class CommentsService {
  constructor() {}

  private readonly http = inject(HttpClient);

  getBlogComments(blogId: string): Observable<any[]> {
    return this.http
      .get<any[]>(
        `${environment.apiUrl}/unrestricted/comments?blog-id=` + blogId
      )
      .pipe(
        tap(() => {
          console.log('successfully retrieved comments for blogId ' + blogId);
        })
      );
  }

  postComment(body: String): Observable<StandardResponseDto> {
    return this.http
      .post<StandardResponseDto>(`${environment.apiUrl}/comments`, body, {
        headers: new HttpHeaders().set('content-type', 'application/json'),
      })
      .pipe(
        tap((data) => {
          const response = data as StandardResponseDto;
          console.log(response.message);
        })
      );
  }

  getCommentsByUser(username: string): Observable<any[]> {
    return this.http
      .get<any[]>(`${environment.apiUrl}/comments/author?username=` + username)
      .pipe(
        tap(() => {
          console.log(
            'successfully retrieved comments added by user ' + username
          );
        })
      );
  }
}
