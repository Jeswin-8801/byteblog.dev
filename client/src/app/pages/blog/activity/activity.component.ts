import { Component, inject } from '@angular/core';
import { NavbarComponent } from '../../../components/navbar/navbar.component';
import { Router } from '@angular/router';
import { AuthService } from '../../../service/auth/auth.service';
import { BlogService } from '../../../service/blog/blog.service';
import { BlogsCompactDto } from '../../../models/dtos/blog/blogs-compact-dto';
import { CommonModule } from '@angular/common';
import { ObjectMapper } from 'json-object-mapper';
import { AuthorCompactDto } from '../../../models/dtos/blog/author-compact-dto';
import { formatDistanceToNow } from 'date-fns';

@Component({
  selector: 'app-activity',
  standalone: true,
  imports: [CommonModule, NavbarComponent],
  templateUrl: './activity.component.html',
})
export class ActivityComponent {
  private readonly blogService = inject(BlogService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  blogs: BlogsCompactDto[] = [];

  ngOnInit() {
    this.getAllBlogsAuthoredByUser();
  }

  private getAllBlogsAuthoredByUser() {
    this.blogService
      .getAllBlogsByUserId(this.authService.user()?.id as string)
      .subscribe({
        next: (responseDto) => {
          console.log('Successfully retreived all blogs authored by user');
          responseDto.forEach((blog) => {
            let blogsDto = ObjectMapper.deserialize(BlogsCompactDto, blog);
            blogsDto.author = ObjectMapper.deserialize(
              AuthorCompactDto,
              blog.author
            );
            blogsDto.timeSinceCreation = formatDistanceToNow(
              new Date(blogsDto.timeSinceCreation as string),
              { addSuffix: true }
            );
            this.blogs.push(blogsDto);
          });
        },
        error: (response) => {
          console.log('Error on post blog:', response.error);
        },
      });
  }

  redirectToPostBlog() {
    this.router.navigateByUrl('/blog/post-blog');
  }
}
