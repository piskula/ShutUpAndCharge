import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { ApiModule, BASE_PATH } from '@trucker/api';
import { HttpClientModule, HttpClientXsrfModule } from '@angular/common/http';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),

    // make generated services working
    importProvidersFrom(
      ApiModule, HttpClientModule, HttpClientXsrfModule
    ),
    { provide: BASE_PATH, useValue: '.' },
  ],
};
