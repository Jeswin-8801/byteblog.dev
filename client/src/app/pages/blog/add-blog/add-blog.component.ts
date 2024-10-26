import { Component, inject } from '@angular/core';
import { NavbarComponent } from '../../../components/navbar/navbar.component';
import { BlogService } from '../../../service/blog/blog.service';
import { CommonModule } from '@angular/common';
import { map, Observable, startWith, Subscription } from 'rxjs';
import {
  FormArray,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MarkdownComponent, MarkdownService } from 'ngx-markdown';
import { PrismService } from '../../../service/blog/prism.service';
import { AuthService } from '../../../service/auth/auth.service';
import { PostBlogDto } from '../../../models/dtos/blog/post-blog-dto';
import { AlertModal } from '../../../components/alert-modal/alert-modal';
import { AlertModalComponent } from '../../../components/alert-modal/alert-modal.component';
import { Utility } from '../../../utility/utility';
import { AlertModalService } from '../../../components/alert-modal/alert-modal.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-add-blog',
  standalone: true,
  imports: [
    CommonModule,
    NavbarComponent,
    AlertModalComponent,
    FormsModule,
    ReactiveFormsModule,
    MarkdownComponent,
  ],
  templateUrl: './add-blog.component.html',
})
export class AddBlogComponent {
  private readonly blogService = inject(BlogService);
  private readonly markdownService = inject(MarkdownService);
  private readonly prismService = inject(PrismService);
  private readonly authService = inject(AuthService);
  private readonly alertModalService = inject(AlertModalService);
  private readonly router = inject(Router);

  subscription!: Subscription;

  postBlogDto = new PostBlogDto();
  postBlogForm!: FormGroup;

  allTags!: { id: number; name: string }[];
  searchTags: string = '';
  filteredTags!: Observable<{ id: number; name: string }[]>;
  isTagsDataLoaded = false;

  isSelectPrimaryTagDropdownClosed: boolean = true;
  primaryTag!: string;

  selectedMarkdownFile: File | undefined;
  markdown!: string;

  selectedImageFiles!: FileList | null;
  imageFilesCompact: { name: string; dataUrl: any }[] = [];

  highlighted = false;

  alertModal: AlertModal = new AlertModal();
  isAlertModalClosed: boolean = true;
  alertMessageGroup: any;

  ngOnInit() {
    this.subscription = this.alertModalService.clickPrimary$.subscribe(
      (isClicked) => {
        if (isClicked && this.alertModal.isPrimaryButtonSubscribedToService) {
          this.router.navigateByUrl(
            '/blog/' +
              this.header?.value
                .replace(/[^a-zA-Z\s]/g, '')
                .replace(/\s+/g, '-')
                .toLowerCase()
          );
        }
      }
    );

    this.getAllTags();
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
    this.postBlogForm = new FormGroup({
      header: new FormControl('', [
        Validators.required,
        Validators.minLength(5),
      ]),
      description: new FormControl('', [
        Validators.required,
        Validators.minLength(25),
      ]),
      searchTag: new FormControl(''),
      tagsBooleanArray: new FormArray(
        this.allTags.map(() => new FormControl(false))
      ),
    });

    this.alertMessageGroup = {
      header: {
        message: '',
        isClosed: true,
      },
      description: {
        message: '',
        isClosed: true,
      },
      selectedTags: {
        message: '',
        isClosed: true,
      },
      primaryTag: {
        message: '',
        isClosed: true,
      },
      markdownFile: {
        message: '',
        isClosed: true,
      },
      imageFiles: {
        message: '',
        isClosed: true,
      },
    };
  }

  onPostBlogFormSubmitted() {
    if (!this.postBlogForm.valid) {
      this.postBlogForm.patchValue({
        header: this.stripEdgeCharacters(this.header!.value),
        description: this.stripEdgeCharacters(this.description!.value),
      });

      if (this.header?.hasError('required')) {
        this.alertMessageGroup.header.message = 'Header cannot be empty';
        this.alertMessageGroup.header.isClosed = false;
      } else if (this.header?.hasError('minlength')) {
        this.alertMessageGroup.header.message =
          'Header must have a length of atleast 5 characters';
        this.alertMessageGroup.header.isClosed = false;
      } else this.alertMessageGroup.header.isClosed = true;

      if (this.description?.hasError('required')) {
        this.alertMessageGroup.description.message =
          'Description cannot be empty';
        this.alertMessageGroup.description.isClosed = false;
      } else if (this.description?.hasError('minlength')) {
        this.alertMessageGroup.description.message =
          'Description must have a length of atleast 25 characters';
        this.alertMessageGroup.description.isClosed = false;
      } else this.alertMessageGroup.description.isClosed = true;
      return;
    }

    this.postBlogDto.userId = this.authService.user()?.id;
    this.postBlogDto.header = this.stripEdgeCharacters(this.header!.value);
    this.postBlogDto.description = this.stripEdgeCharacters(
      this.description!.value
    );

    this.alertMessageGroup.header.isClosed = true;
    this.alertMessageGroup.description.isClosed = true;

    let selectedTags: string[] = [];
    this.tagsBooleanArray.controls.forEach((control, index) => {
      if (control.value) selectedTags.push(this.allTags[index]['name']);
    });

    if (!this.primaryTag) {
      this.alertMessageGroup.primaryTag.message =
        'A primary tag must be assigned';
      this.alertMessageGroup.primaryTag.isClosed = false;
      return;
    }
    this.alertMessageGroup.primaryTag.isClosed = true;

    this.postBlogDto.primaryTag = this.primaryTag;

    if (selectedTags.length > 5) {
      this.alertMessageGroup.selectedTags.message =
        'Cannot have more than 5 tags';
      this.alertMessageGroup.selectedTags.isClosed = false;
      return;
    }
    this.alertMessageGroup.selectedTags.isClosed = true;

    this.postBlogDto.tags = selectedTags;

    if (!this.selectedMarkdownFile) {
      this.alertMessageGroup.markdownFile.message =
        'Markdown file must be present';
      this.alertMessageGroup.markdownFile.isClosed = false;
      return;
    }
    this.alertMessageGroup.markdownFile.isClosed = true;

    this.blogService
      .addNewBlog(
        this.postBlogDto,
        this.selectedMarkdownFile!,
        this.selectedImageFiles!
      )
      .subscribe({
        next: (response) => {
          console.log('Post Blog returned response:', response);
          this.alertModal.set(
            false,
            'Success',
            'Successfully posted Blog ' +
              Utility.getHighlightedText(this.postBlogDto.header!),
            true,
            true,
            'Goto Blog Page',
            true
          );
          if (this.isAlertModalClosed) this.toggleAlertModal();
          this.authService.refreshToken().subscribe({
            next: (tokenDto) => {
              this.authService.storeRefreshedToken(tokenDto);
            },
          });
        },
        error: (response) => {
          console.log('Error on post blog:', response.error);
          if (response.status === 409) {
            this.alertModal.set(
              true,
              'Warning',
              response.error.message,
              false,
              true,
              'OK',
              false
            );
            if (this.isAlertModalClosed) this.toggleAlertModal();
          }
        },
      });
  }

  private stripEdgeCharacters(str: string): string {
    return str.replace(/^[^a-zA-Z0-9?.+-><$\^]+|[^a-zA-Z0-9?.+-><$\^]+$/g, '');
  }

  private getAllTags() {
    this.blogService.getAllTags().subscribe({
      next: (tagsDto) => {
        if (tagsDto.tags)
          this.allTags = tagsDto.tags.map((tag, index) => ({
            id: index,
            name: tag,
          }));

        this.isTagsDataLoaded = true;
        this.createForm();
        this.filterTags();
      },
      error: (response) => {
        console.log('Error on retrieving blog tags', response.error);
      },
    });
  }

  private filterTags() {
    this.filteredTags = this.postBlogForm.controls[
      'searchTag'
    ].valueChanges.pipe(
      startWith(''),
      map((value) => this._filter(value))
    );
  }

  private _filter(value: string): { id: number; name: string }[] {
    const filterValue = value.toLowerCase();
    return this.allTags.filter((tag) =>
      tag['name'].toLowerCase().includes(filterValue)
    );
  }

  onSelectedTagClose(index: number) {
    this.tagsBooleanArray.controls[index].setValue(false);
  }

  lightColors: { name: string; color: string }[] = [
    { name: 'LightCoral', color: '#F7B8B8' },
    { name: 'LightSalmon', color: '#F6BFA9' },
    { name: 'LightGoldenrodYellow', color: '#E6E6AB' },
    { name: 'LightGreen', color: '#A1E9A1' },
    { name: 'LightSkyBlue', color: '#87CEFA' },
    { name: 'Lavender', color: '#E6E6FA' },
    { name: 'PeachPuff', color: '#FFDAB9' },
    { name: 'PapayaWhip', color: '#FFEFD5' },
    { name: 'PaleTurquoise', color: '#AFEEEE' },
    { name: 'PowderBlue', color: '#B0E0E6' },
  ];

  darkColors: { name: string; color: string }[] = [
    { name: 'DarkCoral', color: '#7C1F1F' },
    { name: 'DarkSalmon', color: '#9E553C' },
    { name: 'DarkGoldenrod', color: '#A97C0B' },
    { name: 'DarkGreen', color: '#205E20' },
    { name: 'DarkBlue', color: '#1F1F72' },
    { name: 'DarkSlateBlue', color: '#483D8B' },
    { name: 'Sienna', color: '#A0522D' },
    { name: 'Black', color: '#000000' },
    { name: 'DarkSlateTurquoise', color: '#2F4F4F' },
    { name: 'MidnightBlue', color: '#191970' },
  ];

  getRandomLightColor(index: number) {
    return this.lightColors[index % this.lightColors.length]['color'];
  }

  getRandomDarkColor(index: number) {
    return this.darkColors[index % this.darkColors.length]['color'];
  }

  onMarkdownFileSelected(event: Event): void {
    const element = event.currentTarget as HTMLInputElement;
    let fileList: FileList | null = element.files;
    if (fileList) this.selectedMarkdownFile = fileList[0];

    if (
      !(
        this.selectedMarkdownFile!.name.endsWith('.md') ||
        this.selectedMarkdownFile!.name.endsWith('.mdx')
      )
    ) {
      this.alertMessageGroup.markdownFile.message =
        'Uploades file cannot have file type other than .md or .mdx';
      this.alertMessageGroup.markdownFile.isClosed = false;
      this.onClickDeleteMarkdownFile();
      return;
    }

    this.selectedMarkdownFile?.text().then((text) => {
      this.markdown = this.markdownService.parse(text) as string;
      this.highlighted = true;
    });
  }

  onClickDeleteMarkdownFile() {
    this.selectedMarkdownFile = undefined;
    this.markdown = '';
    this.highlighted = false;
  }

  onPrimaryTagSelected(index: number) {
    if (!this.isSelectPrimaryTagDropdownClosed) {
      this.primaryTag = this.allTags[index]['name'];
      this.isSelectPrimaryTagDropdownClosed = true;
    }
  }

  onImageFilesSelected(event: Event): void {
    const element = event.currentTarget as HTMLInputElement;
    this.selectedImageFiles = element.files;

    if (this.selectedImageFiles) {
      for (let i = 0; i < this.selectedImageFiles.length; i++) {
        if (!this.hasValidImageExtension(this.selectedImageFiles![i].name)) {
          this.alertMessageGroup.imageFiles.message =
            'Uploaded image files cannot have file types other than the ones allowed';
          this.alertMessageGroup.imageFiles.isClosed = false;
          this.onClickDeleteSelectedImageFiles();
          return;
        }
        //Show image preview
        let reader = new FileReader();
        reader.onload = (event: any) => {
          this.imageFilesCompact.push({
            name: this.selectedImageFiles![i].name,
            dataUrl: event.target.result,
          });
        };
        reader.readAsDataURL(this.selectedImageFiles[i]);
      }
    }
  }

  private hasValidImageExtension(fileName: string): boolean {
    return /\.(jpg|jpeg|png|svg|gif)$/i.test(fileName);
  }

  onClickDeleteSelectedImageFiles() {
    this.selectedImageFiles = null;
    this.imageFilesCompact = [];
  }

  toggleAlertModal() {
    this.isAlertModalClosed = !this.isAlertModalClosed;
  }

  get header() {
    return this.postBlogForm.get('header');
  }

  get description() {
    return this.postBlogForm.get('description');
  }

  get searchTag() {
    return this.postBlogForm.get('searchTag');
  }

  get tagsBooleanArray() {
    return this.postBlogForm.get('tagsBooleanArray') as FormArray;
  }
}
