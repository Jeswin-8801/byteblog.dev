import { AfterViewInit, Directive, ElementRef, Renderer2 } from '@angular/core';

@Directive({
  selector: '[appLineNumber]',
  standalone: true,
})
export class LineNumberDirective implements AfterViewInit {
  constructor(private el: ElementRef, private renderer: Renderer2) {}

  ngAfterViewInit(): void {
    this.addLineNumbers(this.el.nativeElement);
  }

  addLineNumbers(element: HTMLElement): void {
    const codeBlocks = element.querySelectorAll('pre code');
    console.log(codeBlocks);
    codeBlocks.forEach((block) => {
      const lines = block.innerHTML.split('\n');
      if (lines.length == 1) {
        block.classList.add('no-line-numbers');
      }
    });
  }
}
