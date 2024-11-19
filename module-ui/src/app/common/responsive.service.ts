import { computed, DestroyRef, Injectable, signal } from '@angular/core';
import { BreakpointObserver } from '@angular/cdk/layout';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { tap } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ResponsiveService {

  private readonly _isDesktop = signal(false);

  public readonly isDesktop = computed(() => this._isDesktop());
  public readonly isMobile = computed(() => !this._isDesktop());

  constructor(
    private readonly breakpointObserver: BreakpointObserver,
    private readonly destroyRef: DestroyRef,
  ) {
    this.breakpointObserver.observe('(min-width: 600px)').pipe(
      tap(state => this._isDesktop.set(state.matches)),
      takeUntilDestroyed(this.destroyRef),
    ).subscribe();
  }

}
