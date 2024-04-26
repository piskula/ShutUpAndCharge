import { ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { inject, Injectable } from '@angular/core';
import { CurrentUserService } from '@trucker/api';
import { catchError, map, Observable, of } from 'rxjs';


@Injectable({providedIn: 'root'})
export class AuthenticationGuard {

  constructor(
    private currentUserService: CurrentUserService,
    private router: Router,
  ) {
  }

  isLoggedIn(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
  ): Observable<boolean | UrlTree> {
    return this.currentUserService.getCurrentUser().pipe(
      // user is logged in, so we can proceed
      map(() => true),
      catchError(() => {
        // user is not logged in, so we route him to welcome page
        return of(this.router.createUrlTree(['app', 'welcome']));
      }),
    );
  }

  isNotLoggedIn(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
  ): Observable<boolean | UrlTree> {
    return this.currentUserService.getCurrentUser().pipe(
      // user is logged in, so we route him into application
      map(() => this.router.createUrlTree(['app', 'inside'])),
      catchError(() => {
        // user is not logged in, so everything is fine
        return of(true);
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
