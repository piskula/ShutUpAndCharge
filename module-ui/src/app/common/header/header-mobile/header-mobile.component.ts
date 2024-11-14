import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, signal } from '@angular/core';
import { MatToolbar } from '@angular/material/toolbar';
import { MatIcon } from '@angular/material/icon';
import { NgForOf, NgIf } from '@angular/common';
import { MatAnchor, MatButton, MatFabAnchor, MatIconButton } from '@angular/material/button';
import { MatChip, MatChipSet } from '@angular/material/chips';
import { tap } from 'rxjs';
import { MatTooltip } from '@angular/material/tooltip';
import { MatMenu, MatMenuTrigger } from '@angular/material/menu';
import { CdkOverlayOrigin } from '@angular/cdk/overlay';
import { MatDivider } from '@angular/material/divider';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthenticationService, CurrentUser } from '../../../security/authentication.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-header-mobile',
  templateUrl: './header-mobile.component.html',
  styleUrl: 'header-mobile.component.scss',
  imports: [
    MatToolbar,
    MatIconButton,
    MatIcon,
    MatChipSet,
    MatChip,
    MatAnchor,
    NgForOf,
    MatFabAnchor,
    NgIf,
    MatTooltip,
    MatMenu,
    MatMenuTrigger,
    MatButton,
    CdkOverlayOrigin,
    MatDivider,
    RouterLinkActive,
    RouterLink,
  ],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HeaderMobileComponent implements OnInit {

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
