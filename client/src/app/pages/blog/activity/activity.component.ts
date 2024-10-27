import { Component, inject } from '@angular/core';
import { NavbarComponent } from '../../../components/navbar/navbar.component';
import { Router } from '@angular/router';
import { AuthService } from '../../../service/auth/auth.service';
import { BlogService } from '../../../service/blog/blog.service';
import { BlogsCompactDto } from '../../../models/dtos/blog/blogs-compact-dto';
import { CommonModule } from '@angular/common';
import { ObjectMapper } from 'json-object-mapper';
import { AuthorCompactDto } from '../../../models/dtos/blog/author-compact-dto';
import { BlogCardComponent } from '../../../components/blog-card/blog-card.component';
import { CommentsComponent } from '../../../components/comments/comments.component';
import { CommentsService } from '../../../service/comments/comments.service';
import { Utility } from '../../../utility/utility';
import { Comment } from '../../../models/comment';

@Component({
  selector: 'app-activity',
  standalone: true,
  imports: [
    CommonModule,
    NavbarComponent,
    BlogCardComponent,
    CommentsComponent,
  ],
  templateUrl: './activity.component.html',
})
export class ActivityComponent {
  private readonly blogService = inject(BlogService);
  private readonly authService = inject(AuthService);
  private readonly commentsService = inject(CommentsService);
  private readonly router = inject(Router);

  blogs: BlogsCompactDto[] = [];
  blogsFound: boolean = true;

  comments!: Comment[];

  ngOnInit() {
    this.getAllBlogsAuthoredByUser();
  }

  private getAllBlogsAuthoredByUser() {
    this.blogService
      .getAllBlogsByUsername(this.authService.user()?.username as string)
      .subscribe({
        next: (responseDto) => {
          console.log('Successfully retreived all blogs authored by user');
          responseDto.forEach((blog) => {
            let blogsDto = ObjectMapper.deserialize(BlogsCompactDto, blog);
            blogsDto.author = ObjectMapper.deserialize(
              AuthorCompactDto,
              blog.author
            );
            this.blogs.push(blogsDto);
          });

          // sort by latest
          this.blogs.sort(
            (a, b) =>
              new Date(b.timeSinceCreation as string).getTime() -
              new Date(a.timeSinceCreation as string).getTime()
          );

          // get all comments
          this.getComments();
        },
        error: (response) => {
          console.log('Error on retriving blogs: ', response.error);
          this.blogsFound = false;
        },
      });
  }

  private getComments() {
    this.commentsService
      .getCommentsByUser(this.authService.user()?.username as string)
      .subscribe({
        next: (comments) =>
          (this.comments = Utility.deserializeComments(comments)),
        error: (response) => {
          console.log('Error on get comments for blog: ', response.error);
        },
      });
  }

  redirectToPostBlog() {
    this.router.navigateByUrl('/blog/post-blog');
  }
}
