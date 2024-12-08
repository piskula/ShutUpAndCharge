import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, signal } from '@angular/core';
import { MatToolbar } from '@angular/material/toolbar';
import { MatIcon } from '@angular/material/icon';
import { NgIf } from '@angular/common';
import { MatAnchor, MatButton } from '@angular/material/button';
import { MatChip, MatChipSet } from '@angular/material/chips';
import { tap } from 'rxjs';
import { MatTooltip } from '@angular/material/tooltip';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthenticationService, CurrentUser } from '../../../security/authentication.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-header-desktop',
  templateUrl: './header-desktop.component.html',
  styleUrl: 'header-desktop.component.scss',
  imports: [
    MatToolbar,
    MatIcon,
    MatChipSet,
    MatChip,
    MatAnchor,
    NgIf,
    MatButton,
    MatTooltip,
    RouterLinkActive,
    RouterLink,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HeaderDesktopComponent implements OnInit {

  public currentUser = signal<CurrentUser | null>(null);

  constructor(
    private readonly authenticationService: AuthenticationService,
    private readonly destroyRef: DestroyRef,
  ) {
  }

  ngOnInit(): void {
    this.authenticationService.currentUser().pipe(
      tap(currentUser => this.currentUser.set(currentUser)),
      takeUntilDestroyed(this.destroyRef),
    ).subscribe();
  }

}
