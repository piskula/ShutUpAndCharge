import {
  ApplicationConfig,
  DEFAULT_CURRENCY_CODE,
  importProvidersFrom,
  LOCALE_ID,
  provideZoneChangeDetection,
} from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { ApiModule, BASE_PATH } from '@suac/api';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { MAT_DIALOG_DEFAULT_OPTIONS } from '@angular/material/dialog';

// locale
import { registerLocaleData } from '@angular/common';
import localeSk from '@angular/common/locales/sk';
import { errorInterceptor } from './common/error.interceptor';
registerLocaleData(localeSk);

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),

    provideHttpClient(  // make generated services working
      withInterceptors([errorInterceptor])
    ),
    provideAnimationsAsync(), // needed for material table sort header arrows
    // default sizing for modals
    { provide: MAT_DIALOG_DEFAULT_OPTIONS, useValue: { width: '50%', minWidth: '22rem', maxWidth: '30rem' } },
    importProvidersFrom(ApiModule),
    { provide: BASE_PATH, useValue: '.' },
    { provide: LOCALE_ID, useValue: 'sk-SK' }, // TODO this should be taken from browser (?)
    { provide: DEFAULT_CURRENCY_CODE, useValue: 'EUR' },
  ],
};
