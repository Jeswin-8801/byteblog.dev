import { CommonModule } from '@angular/common';
import { Component, inject, Input } from '@angular/core';
import { Router } from '@angular/router';
import { Blog } from '../../models/blog';
import { formatDistanceToNow } from 'date-fns';

@Component({
  selector: 'app-blog-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './blog-card.component.html',
})
export class BlogCardComponent {
  private readonly router = inject(Router);

  @Input() blog!: Blog;
  @Input() showAuthorAsYou!: boolean;

  redirectToAuthorPage() {
    console.log(`/blog/author/` + this.blog.author?.username);
    this.router.navigateByUrl(`/blog/author/` + this.blog.author?.username);
  }

  formatHumanReadableTime(time: string) {
    return formatDistanceToNow(new Date(time as string), { addSuffix: true });
  }
}
