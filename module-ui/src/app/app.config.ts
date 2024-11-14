import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { ApiModule, BASE_PATH } from '@suac/api';
import { provideHttpClient } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { MAT_DIALOG_DEFAULT_OPTIONS } from '@angular/material/dialog';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),

    provideHttpClient(), // make generated services working
    provideAnimations(), // needed for material table sort header arrows
    // default sizing for modals
    { provide: MAT_DIALOG_DEFAULT_OPTIONS, useValue: { width: '50%', minWidth: '22rem', maxWidth: '30rem' } },
    importProvidersFrom(ApiModule),
    { provide: BASE_PATH, useValue: '.' },
  ],
};
