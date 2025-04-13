import { Component, inject } from '@angular/core';
import { NavbarComponent } from '../../../components/navbar/navbar.component';
import { CommonModule } from '@angular/common';
import { BlogService } from '../../../service/blog/blog.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Blog } from '../../../models/blog';
import { ObjectMapper } from 'json-object-mapper';
import { format } from 'date-fns';
import { MarkdownComponent, MarkdownService } from 'ngx-markdown';
import { PrismService } from '../../../service/blog/prism.service';
import { AuthorCompactDto } from '../../../models/dtos/blog/author-compact-dto';
import { LineNumberDirective } from '../../../service/blog/line-number.directive';
import { Comment } from '../../../models/comment';
import { CommentsComponent } from '../../../components/comments/comments.component';
import { AuthService } from '../../../service/auth/auth.service';
import {
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { CommentsService } from '../../../service/comments/comments.service';
import { PostCommentDto } from '../../../models/dtos/comment/post-comment-dto';
import { Utility } from '../../../utility/utility';

@Component({
  selector: 'app-blog',
  standalone: true,
  imports: [
    CommonModule,
    NavbarComponent,
    MarkdownComponent,
    CommentsComponent,
    FormsModule,
    ReactiveFormsModule,
    LineNumberDirective,
  ],
  templateUrl: './blog.component.html',
})
export class BlogComponent {
  private readonly blogService = inject(BlogService);
  private readonly commentsService = inject(CommentsService);
  private readonly authService = inject(AuthService);
  private readonly markdownService = inject(MarkdownService);
  private readonly prismService = inject(PrismService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  headingUri!: string;
  blog!: Blog;

  markdown!: string;
  highlighted = false;

  postCommentForm!: FormGroup;
  comments: Comment[] = [];

  alertMessage!: string;
  isAlertClosed: boolean = true;

  ngOnInit() {
    this.route.paramMap.subscribe((params) => {
      this.headingUri = params.get('headingUri') as string;
    });

    if (this.headingUri) this.getBlogByHeading();

    this.createForm();
  }

  ngAfterViewInit() {
    this.prismService.highlightAll();
  }

  ngAfterViewChecked() {
    if (this.highlighted) {
      this.prismService.highlightAll();
      this.highlighted = false;
    }
  }

  private createForm() {
    this.postCommentForm = new FormGroup({
      comment: new FormControl('', [
        Validators.required,
        Validators.minLength(5),
      ]),
    });
  }

  onPostCommentFormSubmitted() {
    if (!this.postCommentForm.valid) {
      if (this.isAlertClosed) this.toggleAlert();
      if (this.comment?.hasError('required'))
        this.alertMessage = 'Comment cannot be empty';
      else if (this.comment?.hasError('minlength'))
        this.alertMessage = 'Must have atleast 5 characters';
      return;
    }

    let postCommentDto = new PostCommentDto();
    postCommentDto.comment = this.comment?.value;
    postCommentDto.userId = this.authService.user()?.id;
    postCommentDto.blogId = this.blog.id;
    this.commentsService
      .postComment(ObjectMapper.serialize(postCommentDto))
      .subscribe({
        next: () => {
          // refresh comments
          this.getComments();
          this.comment?.setValue('');
          this.comment?.markAsUntouched();
        },
      });
  }

  private getBlogByHeading() {
    this.blogService.getBlogByHeading(this.headingUri).subscribe({
      next: (responseDto) => {
        console.log('Successfully retreived blog ' + this.headingUri);

        this.blog = ObjectMapper.deserialize(Blog, responseDto);
        this.blog.author = ObjectMapper.deserialize(
          AuthorCompactDto,
          responseDto.author
        );
        this.blog.timeSinceCreation = format(
          new Date(this.blog.timeSinceCreation as string),
          'do MMMM, yyyy'
        );

        this.compileMarkdown();
        this.getComments();
      },
      error: (response) => {
        console.log('Error on post blog:', response.error);
      },
    });
  }

  private compileMarkdown() {
    this.blogService
      .getMarkdownFileFromUrl(this.blog.markdownFileUrl!)
      .subscribe({
        next: (text) => {
          this.markdown = this.markdownService.parse(text) as string;
          this.highlighted = true;
        },
        error: (response) => {
          console.log('Error on GET markdownfile', response.error);
        },
      });
  }

  private getComments() {
    this.commentsService.getBlogComments(this.blog.id as string).subscribe({
      next: (comments) =>
        (this.comments = Utility.deserializeComments(comments)),
      error: (response) => {
        console.log('Error on get comments for blog: ', response.error);
      },
    });
  }

  isLoggedIn() {
    return this.authService.isAuthenticated();
  }

  redirectToAuthorPage() {
    this.router.navigateByUrl('blog/author/' + this.blog.author?.username);
  }

  toggleAlert() {
    this.isAlertClosed = !this.isAlertClosed;
  }

  handleReply(replyAdded: boolean) {
    if (replyAdded) {
      this.getComments();
    }
  }

  get comment() {
    return this.postCommentForm.get('comment');
  }
}
