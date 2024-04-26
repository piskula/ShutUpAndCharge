import { Routes } from '@angular/router';
import { WelcomeComponent } from './welcome/welcome.component';
import { isLoggedIn, isNotLoggedIn } from './security/authentication.guard';
import { InsideSecuredComponent } from './app2/app-inside-secured.component';
import { NotFoundComponent } from './not-found/not-found.component';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'app/welcome',
  },
  {
    path: 'app',
    pathMatch: 'full',
    redirectTo: 'app/welcome',
  },
  {
    path: 'app/welcome',
    component: WelcomeComponent,
    canActivate: [isNotLoggedIn],
  },
  {
    path: 'app/inside',
    component: InsideSecuredComponent,
    canActivate: [isLoggedIn],
  },
  {
    path: '**',
    component: NotFoundComponent,
  },
];
