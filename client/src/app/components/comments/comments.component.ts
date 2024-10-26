import {
  Component,
  EventEmitter,
  inject,
  Input,
  Output,
  SimpleChanges,
} from '@angular/core';
import { Comment } from '../../models/comment';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../service/auth/auth.service';
import { format, formatDistanceToNow } from 'date-fns';
import {
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { PostCommentDto } from '../../models/dtos/comment/post-comment-dto';
import { CommentsService } from '../../service/comments/comments.service';
import { ObjectMapper } from 'json-object-mapper';
import { Router } from '@angular/router';

@Component({
  selector: 'app-comments',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './comments.component.html',
})
export class CommentsComponent {
  private readonly authService = inject(AuthService);
  private readonly commentsService = inject(CommentsService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

  @Input() comments!: Comment[];
  @Input() blogId!: string;
  @Input() showReplyOption: boolean = true;

  @Output() replyAdded = new EventEmitter<boolean>();

  currentUser!: string;
  showCommentsOptionsDropdownList!: boolean[];

  formsArray!: FormArray;
  alertArray!: { showAlert: boolean; alertMessage: string }[];
  showReplyBoxArray!: boolean[];

  ngOnInit() {
    this.currentUser = this.authService.user()?.username as string;
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['comments']) {
      if (this.comments) {
        // initialize showCommentsOptionsDropdownList
        this.showCommentsOptionsDropdownList = new Array(
          this.comments.length
        ).fill(false);

        // initilize FormArray
        this.formsArray = this.fb.array([]);
        this.comments.forEach(() =>
          this.formsArray.push(
            new FormGroup({
              comment: new FormControl('', [
                Validators.required,
                Validators.minLength(5),
              ]),
            })
          )
        );

        // initialize Alert Array
        this.alertArray = new Array(this.comments.length).fill({
          showAlert: false,
          alertMessage: '',
        });

        // initialize showReplyBoxArray
        this.showReplyBoxArray = new Array(this.comments.length).fill(false);
      }
    }
  }

  postCommentOnSubmit(index: number) {
    if (!this.formsArray.controls[index].valid) {
      if (!this.alertArray[index].showAlert) this.toggleAlert(index);
      if (this.getComment(index).getError('required'))
        this.alertArray[index].alertMessage = 'Comment cannot be empty';
      else if (this.getComment(index).hasError('minlength'))
        this.alertArray[index].alertMessage = 'Must have atleast 5 characters';
      return;
    }

    let postCommentDto = new PostCommentDto();
    postCommentDto.comment = this.getComment(index)?.value;
    postCommentDto.userId = this.authService.user()?.id;
    postCommentDto.blogId = this.blogId;
    postCommentDto.parentCommentId = this.comments[index].id;
    this.commentsService
      .postComment(ObjectMapper.serialize(postCommentDto))
      .subscribe({ next: () => this.replyAdded.emit(true) });
  }

  isLoggedIn() {
    return this.authService.isAuthenticated();
  }

  toggleCloseCommentsOptionsDropdown(index: number) {
    this.showCommentsOptionsDropdownList[index] =
      !this.showCommentsOptionsDropdownList[index];
  }

  toggleAlert(index: number) {
    this.alertArray[index].showAlert = !this.alertArray[index].showAlert;
  }

  toggleReplyBox(index: number) {
    this.showReplyBoxArray[index] = !this.showReplyBoxArray[index];
  }

  showAlert(index: number): boolean {
    return this.alertArray[index]['showAlert'];
  }

  alertMessage(index: number): string {
    return this.alertArray[index]['alertMessage'];
  }

  getComment(index: number) {
    return this.getFormGroupFromIndex(index).get('comment') as FormControl;
  }

  getFormGroupFromIndex(index: number) {
    return this.formsArray.at(index) as FormGroup;
  }

  redirectToAuthorPage(index: number) {
    this.router.navigateByUrl(
      '/blog/author/' + this.comments[index].author?.username
    );
  }

  handleReplyAdded(replyAdded: boolean) {
    if (replyAdded) this.replyAdded.emit(true);
  }

  formatHumanReadableDate(date: string) {
    return formatDistanceToNow(new Date(date), { addSuffix: true });
  }
}
