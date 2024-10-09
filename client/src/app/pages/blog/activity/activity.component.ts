import { Component, inject } from '@angular/core';
import { NavbarComponent } from '../../../components/navbar/navbar.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-activity',
  standalone: true,
  imports: [NavbarComponent],
  templateUrl: './activity.component.html',
})
export class ActivityComponent {
  private readonly router = inject(Router);

  redirectToPostBlog() {
    this.router.navigateByUrl('/blog/post-blog');
  }
}
