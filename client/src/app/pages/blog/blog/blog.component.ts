import { Component, inject } from '@angular/core';
import { NavbarComponent } from '../../../components/navbar/navbar.component';
import { CommonModule } from '@angular/common';
import { BlogService } from '../../../service/blog/blog.service';
import { ActivatedRoute } from '@angular/router';
import { Blog } from '../../../models/blog';
import { ObjectMapper } from 'json-object-mapper';
import { format } from 'date-fns';
import { MarkdownComponent, MarkdownService } from 'ngx-markdown';
import { PrismService } from '../../../service/blog/prism.service';
import { AuthorCompactDto } from '../../../models/dtos/blog/author-compact-dto';
import { LineNumberDirective } from '../../../service/blog/line-number.directive';

@Component({
  selector: 'app-blog',
  standalone: true,
  imports: [
    CommonModule,
    NavbarComponent,
    MarkdownComponent,
    LineNumberDirective,
  ],
  templateUrl: './blog.component.html',
})
export class BlogComponent {
  private readonly blogService = inject(BlogService);
  private readonly markdownService = inject(MarkdownService);
  private readonly prismService = inject(PrismService);
  private readonly route = inject(ActivatedRoute);

  headingUri!: string;
  blog!: Blog;

  markdown!: string;
  highlighted = false;

  ngOnInit() {
    this.route.paramMap.subscribe((params) => {
      this.headingUri = params.get('headingUri') as string;
    });

    if (this.headingUri) this.getBlogByHeading();
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
}
