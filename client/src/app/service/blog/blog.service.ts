import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TagsDto } from '../../models/dtos/blog/tags-dto';
import { PostBlogDto } from '../../models/dtos/blog/post-blog-dto';
import { StandardResponseDto } from '../../models/dtos/user/standard-response-dto';
import { ObjectMapper } from 'json-object-mapper';

@Injectable({
  providedIn: 'root',
})
export class BlogService {
  constructor() {}

  private readonly http = inject(HttpClient);

  getAllTags(): Observable<TagsDto> {
    return this.http
      .get<TagsDto>(`${environment.apiUrl}/blog/get-all-tags`)
      .pipe(
        tap(() => {
          console.log('successfully retrieved blog tags');
        })
      );
  }

  addNewBlog(
    blog: PostBlogDto,
    markdownFile: File,
    images: FileList
  ): Observable<StandardResponseDto> {
    const formData: FormData = new FormData();
    formData.append(
      'blog',
      new Blob([ObjectMapper.serialize(blog) as string], {
        type: 'application/json',
      })
    );
    formData.append('markdown', markdownFile);

    if (images)
      for (let i = 0; i < images.length; i++)
        formData.append('images', images[i]);

    return this.http.post<StandardResponseDto>(
      `${environment.apiUrl}/blog`,
      formData
    );
  }
}
