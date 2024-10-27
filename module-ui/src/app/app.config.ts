import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { ApiModule, BASE_PATH } from '@suac/api';
import { provideHttpClient } from '@angular/common/http';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),

    // make generated services working
    provideHttpClient(),
    importProvidersFrom(ApiModule),
    { provide: BASE_PATH, useValue: '.' },
  ],
};
