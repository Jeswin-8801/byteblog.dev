import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { NavbarComponent } from '../../../components/navbar/navbar.component';
import { ActivatedRoute } from '@angular/router';
import { BlogService } from '../../../service/blog/blog.service';
import { ObjectMapper } from 'json-object-mapper';
import { BlogsCompactDto } from '../../../models/dtos/blog/blogs-compact-dto';
import { AuthorCompactDto } from '../../../models/dtos/blog/author-compact-dto';
import { Blog } from '../../../models/blog';
import { AuthorDto } from '../../../models/dtos/blog/author-dto';
import { BlogCardComponent } from '../../../components/blog-card/blog-card.component';

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [CommonModule, NavbarComponent, BlogCardComponent],
  templateUrl: './user.component.html',
})
export class UserComponent {
  private readonly blogService = inject(BlogService);
  private readonly route = inject(ActivatedRoute);

  username!: string;
  author!: AuthorDto;
  blogs: Blog[] = [];

  ngOnInit() {
    this.route.paramMap.subscribe((params) => {
      this.username = params.get('username') as string;
    });

    if (this.username) {
      this.getAuthorDetails();
      this.getAllBlogsByAuthor();
    }
  }

  private getAuthorDetails() {
    this.blogService.getAuthorDetails(this.username).subscribe({
      next: (responseDto) => {
        console.log('Successfully retreived author details');
        this.author = ObjectMapper.deserialize(AuthorDto, responseDto);
      },
      error: (response) => {
        console.log('Error on GET authordetails', response.error);
      },
    });
  }

  private getAllBlogsByAuthor() {
    this.blogService.getAllBlogsByUsername(this.username).subscribe({
      next: (responseDto) => {
        console.log('Successfully retreived all blogs authored by user');
        responseDto.forEach((blog) => {
          let blogsDto = ObjectMapper.deserialize(BlogsCompactDto, blog);
          blogsDto.author = ObjectMapper.deserialize(
            AuthorCompactDto,
            blog.author
          );
          this.blogs.push(blogsDto);

          // sort by latest
          this.blogs.sort(
            (a, b) =>
              new Date(b.timeSinceCreation as string).getTime() -
              new Date(a.timeSinceCreation as string).getTime()
          );
        });
      },
      error: (response) => {
        console.log('Error on retriving blogs: ', response.error);
      },
    });
  }
}
