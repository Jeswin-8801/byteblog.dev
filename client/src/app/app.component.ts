import { Component, HostListener, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { UtilitiesService } from './service/utilities/utilities.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
})
export class AppComponent {
  private readonly utilitiesService = inject(UtilitiesService);

  title = 'client';

  @HostListener('document:click', ['$event'])
  documentClick(event: any): void {
    this.utilitiesService.documentClickedTarget.next(event.target);
  }
}
