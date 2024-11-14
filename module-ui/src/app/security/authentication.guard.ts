import { ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { ROUTE_PREFIX_AUTH, ROUTE_PREFIX_NO_AUTH } from '../app.routes';
import { AuthenticationService } from './authentication.service';


@Injectable({providedIn: 'root'})
export class AuthenticationGuard {

  constructor(
    private authenticationService: AuthenticationService,
    private router: Router,
  ) {
  }

  isLoggedIn(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
  ): Observable<boolean | UrlTree> {
    return this.authenticationService.isLoggedIn().pipe(
      map(isLoggedIn => {
        if (isLoggedIn) // user is logged in, so we can proceed
          return true;
        else // user is not logged in, so we route him to welcome page
          return this.router.createUrlTree([ROUTE_PREFIX_NO_AUTH]);
      }),
    );
  }

  isNotLoggedIn(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
  ): Observable<boolean | UrlTree> {
    return this.authenticationService.isLoggedIn().pipe(
      map(isLoggedIn => {
        if (isLoggedIn) // user is logged in, so we route him into application
          return this.router.createUrlTree([ROUTE_PREFIX_AUTH]);
        else // user is not logged in, so everything is fine
          return true;
      }),
    );
  }

}

export const isLoggedIn: CanActivateFn = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot,
) => {
  return inject(AuthenticationGuard).isLoggedIn(route, state);
}

export const isNotLoggedIn: CanActivateFn = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot,
) => {
  return inject(AuthenticationGuard).isNotLoggedIn(route, state);
}
