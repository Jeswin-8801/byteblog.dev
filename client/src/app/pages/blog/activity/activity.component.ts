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
import { formatDistanceToNow } from 'date-fns';
import { CommentCompactDto } from '../../../models/dtos/comment/comment-compact-dto';

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
  profileImageUrl!: string;

  comments!: CommentCompactDto[];

  ngOnInit() {
    this.getAllBlogsAuthoredByUser();
    this.profileImageUrl = this.authService.user()?.profileImageUrl as string;
    this.getComments();
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
        next: (comments) => {
          this.comments = ObjectMapper.deserializeArray(
            CommentCompactDto,
            comments
          );
          // sort by latest
          this.comments.sort(
            (a, b) =>
              new Date(b.lastUpdated as string).getTime() -
              new Date(a.lastUpdated as string).getTime()
          );
          console.log(this.comments);
        },
        error: (response) => {
          console.log('Error on get comments for blog: ', response.error);
        },
      });
  }

  redirectToPostBlog() {
    this.router.navigateByUrl('/blog/post-blog');
  }

  redirectToBlogPage(uri: string) {
    this.router.navigateByUrl(`/blog/` + uri);
  }

  formatHumanReadableDate(date: string) {
    return formatDistanceToNow(new Date(date), { addSuffix: true });
  }
}
