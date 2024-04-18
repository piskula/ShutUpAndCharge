import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { ApiModule, BASE_PATH } from '@trucker/api';
import { HttpClientModule } from "@angular/common/http";

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),

    // make generated services working
    importProvidersFrom(
      ApiModule, HttpClientModule
    ),
    { provide: BASE_PATH, useValue: 'http://localhost:4200' },
  ],
};
