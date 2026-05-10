import { computed, Injectable, Signal, signal } from '@angular/core';
import { CurrentUserService, CurrentUserDTO } from '@suac/api';
import { catchError, filter, map, of, take, tap } from 'rxjs';
import { toObservable } from '@angular/core/rxjs-interop';
import { keycloakService } from './keycloak.service';

export interface CurrentUser {
  id: string;
  name: string;
  roles: string[];
}

@Injectable({ providedIn: 'root' })
export class AuthenticationService {

  private readonly isServiceInitiated = signal(false);
  private readonly state = signal<CurrentUserDTO | null>(null);

  private readonly _loggedIn = computed(() => (this.state()?.id ?? 0) > 0);
  private readonly _currentUser: Signal<CurrentUser | null> = computed(() => {
    const user = this.state();
    if (!user) return null;

    return {
      id: user.idKeycloak,
      name: `${user.firstName} ${user.lastName}`,
      roles: (user.roles ?? []).map(r => r.toString()),
    } as CurrentUser;
  });

  readonly currentUserValue: Signal<CurrentUser | null> = computed(() => this._currentUser());
  readonly isAdmin = computed(() =>
    this.currentUserValue()?.roles?.includes('Admin') ?? false
  );

  constructor(
    private currentUserService: CurrentUserService,
  ) {
    this.init();
  }

  private async init() {
    // wait for keycloak init
    while (!keycloakService.isInitialized()) {
      await new Promise(r => setTimeout(r, 10));
    }

    if (keycloakService.isLoggedIn()) {
      this.refreshStore();
    } else {
      this.setUser(null);
    }
  }

  isLoggedIn() {
    return toObservable(this.isServiceInitiated).pipe(
      filter(v => v),
      map(() => this._loggedIn())
    );
  }

  login() {
    return keycloakService.login();
  }

  logout() {
    return keycloakService.logout();
  }

  private refreshStore(): void {
    this.currentUserService.getCurrentUser().pipe(
      take(1),
      tap(user => this.setUser(user)),
      catchError(() => {
        this.setUser(null);
        return of(null);
      }),
    ).subscribe();
  }

  private setUser(user: CurrentUserDTO | null) {
    this.state.set(user);
    this.isServiceInitiated.set(true);
  }
}
