import { computed, Injectable, Signal, signal } from '@angular/core';
import { CurrentUserService, CurrentUserDTO } from '@suac/api';
import { catchError, combineLatest, filter, map, Observable, of, take, tap } from 'rxjs';
import { toObservable } from '@angular/core/rxjs-interop';

export interface CurrentUser {
  id: number;
  name: string;
  roles: string[];
}

@Injectable({providedIn: 'root'})
export class AuthenticationService {

  private readonly isServiceInitiated = signal(false);
  private readonly state = signal<CurrentUserDTO | null>(null);

  private readonly _loggedIn = computed(() => (this.state()?.id ?? 0) > 0);
  private readonly _currentUser: Signal<CurrentUser | null> = computed(() => {
    const user = this.state();
    if (user == null)
      return null

    const rolesEnum: CurrentUserDTO.RolesEnum[] = user.roles ?? [];
    return {
      id: user.id,
      name: `${user.firstName} ${user.lastName}`,
      roles: rolesEnum.map((r: CurrentUserDTO.RolesEnum) => r.toString()),
    } as CurrentUser;
  });
  private readonly currentUser$ = toObservable(this._currentUser);

  constructor(
    private currentUserService: CurrentUserService,
  ) {
    this.refreshStore();
  }

  isLoggedIn(): Observable<boolean> {
    return combineLatest([
      toObservable(this.isServiceInitiated),
      toObservable(this._loggedIn),
    ]).pipe(
      filter(([isServiceInitiated, _]) => isServiceInitiated),
      map(([_, loggedIn]) => loggedIn),
    );
  }

  currentUser(): Observable<CurrentUser | null> {
    return this.currentUser$;
  }

  private refreshStore(): void {
    this.currentUserService.getCurrentUser().pipe(
      take(1),
      tap((currentUser: CurrentUserDTO) => {
        this.setUser(currentUser);
      }),
      catchError(() => {
        this.setUser(null);
        return of({});
      }),
    ).subscribe();
  }

  private setUser(currentUser: CurrentUserDTO | null) {
    this.state.set(currentUser);
    this.isServiceInitiated.set(true);
  }

}
