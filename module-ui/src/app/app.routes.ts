import { Routes } from '@angular/router';
import { isLoggedIn, isNotLoggedIn } from './security/authentication.guard';
import { NotFoundComponent } from './not-found/not-found.component';

export const ROUTE_PREFIX_AUTH = 'auth';
export const ROUTE_PREFIX_NO_AUTH = 'noAuth';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: ROUTE_PREFIX_AUTH,
  },
  {
    path: ROUTE_PREFIX_NO_AUTH,
    canActivate: [isNotLoggedIn],
    loadChildren: async() => import('./non-authenticated/non-authenticated.module').then(m => m.NonAuthenticatedModule),
  },
  {
    path: ROUTE_PREFIX_AUTH,
    canActivate: [isLoggedIn],
    loadChildren: async() => import('./authenticated/authenticated.module').then(m => m.AuthenticatedModule),
  },
  {
    path: '**',
    component: NotFoundComponent,
  },
];
