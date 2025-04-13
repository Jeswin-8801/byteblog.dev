import { Component, inject } from '@angular/core';
import { AuthService } from '../../../service/auth/auth.service';
import { NavbarComponent } from '../../../components/navbar/navbar.component';
import { AlertModal } from '../../../components/alert-modal/alert-modal';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { AlertModalComponent } from '../../../components/alert-modal/alert-modal.component';
import { Utility } from '../../../utility/utility';
import { CommonModule, Location } from '@angular/common';
import { AlertModalService } from '../../../components/alert-modal/alert-modal.service';
import { Subscription } from 'rxjs';
import { BlogService } from '../../../service/blog/blog.service';
import { BlogsCompactDto } from '../../../models/dtos/blog/blogs-compact-dto';
import { ObjectMapper } from 'json-object-mapper';
import { BlogCardComponent } from '../../../components/blog-card/blog-card.component';
import { AuthorCompactDto } from '../../../models/dtos/blog/author-compact-dto';

@Component({
  selector: 'app-explore',
  standalone: true,
  imports: [
    CommonModule,
    AlertModalComponent,
    NavbarComponent,
    BlogCardComponent,
  ],
  templateUrl: './explore.component.html',
})
export class ExploreComponent {
  private readonly authService = inject(AuthService);
  private readonly blogService = inject(BlogService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly location = inject(Location);

  private readonly alertModalService = inject(AlertModalService);
  subscription!: Subscription;

  alertModal: AlertModal = new AlertModal();
  isAlertModalClosed: boolean = true;

  featuredBlog!: BlogsCompactDto;
  latestBlogs: BlogsCompactDto[] = [];
  page = 0;
  loading = false;

  param!: string;

  ngOnInit() {
    this.subscription = this.alertModalService.clickPrimary$.subscribe(
      (isClicked) => {
        if (isClicked && this.alertModal.isPrimaryButtonSubscribedToService)
          Utility.removeQueryParams(this.location, this.param);
      }
    );
    this.handleQueryParams();
    this.getFeaturedBlog();
  }

  private handleQueryParams() {
    this.route.queryParams.subscribe((params) => {
      if (params['registered']) {
        this.setOauthSuccessMessage(params);
        this.param = 'registered';
      }
    });
  }

  private setOauthSuccessMessage(params: Params) {
    this.toggleAlertModal();

    const user = this.authService.user();
    if (params['registered'] === 'Success')
      this.alertModal.set(
        false,
        'You are good to go!',
        'Successfully linked your ' +
          Utility.getHighlightedText(user?.authProvider!) +
          ' account ' +
          Utility.getHighlightedText(user?.email!),
        false,
        true,
        'OK',
        true
      );
  }

  private getFeaturedBlog() {
    this.blogService.getFeaturedBlog().subscribe({
      next: (response) => {
        this.featuredBlog = ObjectMapper.deserialize(BlogsCompactDto, response);
        this.loadLatestBlogs();
      },
      error: (response) => console.log(response),
    });
  }

  private loadLatestBlogs() {
    this.loading = true;
    this.blogService.getLatestBlogsAsPageable(this.page).subscribe({
      next: (data) => {
        console.log('Successfully retreived all blogs authored by user');
        let retrievedBlogs: BlogsCompactDto[] = [];
        data.forEach((blog: any) => {
          let blogsDto = ObjectMapper.deserialize(BlogsCompactDto, blog);
          blogsDto.author = ObjectMapper.deserialize(
            AuthorCompactDto,
            blog.author
          );
          if (this.featuredBlog.heading !== blogsDto.heading)
            retrievedBlogs.push(blogsDto);
        });

        // sort by latest
        retrievedBlogs.sort(
          (a, b) =>
            new Date(b.timeSinceCreation as string).getTime() -
            new Date(a.timeSinceCreation as string).getTime()
        );

        this.latestBlogs = [...this.latestBlogs, ...retrievedBlogs];
        this.loading = false;
        this.page++;
      },
      error: (response) => console.log(response),
    });
  }

  onScroll() {
    if (!this.loading) this.loadLatestBlogs;
  }

  toggleAlertModal() {
    this.isAlertModalClosed = !this.isAlertModalClosed;
  }

  redirectToBlogPost(uri: string) {
    this.router.navigateByUrl(`blog/` + uri);
  }

  ngOnDestroy() {
    // prevent memory leak when component destroyed
    this.subscription.unsubscribe();
  }
}
