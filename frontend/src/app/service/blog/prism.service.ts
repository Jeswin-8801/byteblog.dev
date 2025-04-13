import { isPlatformBrowser } from '@angular/common';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';

import * as Prism from 'prismjs';

import 'prismjs/components/prism-csharp';
import 'prismjs/components/prism-css';
import 'prismjs/components/prism-java';
import 'prismjs/components/prism-rust';
import 'prismjs/components/prism-javascript';
import 'prismjs/components/prism-json';
import 'prismjs/components/prism-markup';
import 'prismjs/components/prism-python';
import 'prismjs/components/prism-sass';
import 'prismjs/components/prism-scss';
import 'prismjs/components/prism-sql';
import 'prismjs/components/prism-typescript';
import 'prismjs/components/prism-bash';
import 'prismjs/components/prism-toml';
import 'prismjs/components/prism-yaml';
import 'prismjs/components/prism-docker';
import 'prismjs/components/prism-properties';
import 'prismjs/components/prism-xml-doc';
import 'prismjs/components/prism-go';
import 'prismjs/components/prism-latex';
import 'prismjs/components/prism-markup';
import 'prismjs/components/prism-markdown';
import 'prismjs/components/prism-diff';
import 'prismjs/components/prism-cshtml';

import 'prismjs/plugins/toolbar/prism-toolbar';
import 'prismjs/plugins/data-uri-highlight/prism-data-uri-highlight';
import 'prismjs/plugins/line-numbers/prism-line-numbers';
import 'prismjs/plugins/line-highlight/prism-line-highlight';
import 'prismjs/plugins/copy-to-clipboard/prism-copy-to-clipboard';

// eslint-disable-next-line @typescript-eslint/no-explicit-any
// declare const Prism: any;

@Injectable({
  providedIn: 'root', // can inject anywahere
})
export class PrismService {
  constructor(@Inject(PLATFORM_ID) private platformId: object) {}

  highlightAll() {
    if (isPlatformBrowser(this.platformId)) {
      Prism.highlightAll();
    }
  }
}
