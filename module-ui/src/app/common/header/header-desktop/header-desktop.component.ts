import { ChangeDetectionStrategy, Component, computed } from '@angular/core';
import { MatToolbar } from '@angular/material/toolbar';
import { MatIcon } from '@angular/material/icon';
import { MatAnchor, MatButton } from '@angular/material/button';
import { MatChip, MatChipSet } from '@angular/material/chips';
import { MatTooltip } from '@angular/material/tooltip';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthenticationService } from '../../../security/authentication.service';

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
    MatButton,
    MatTooltip,
    RouterLinkActive,
    RouterLink,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HeaderDesktopComponent {

  public readonly currentUser = computed(() => this.authenticationService.currentUserValue());

  constructor(
    private readonly authenticationService: AuthenticationService,
  ) {
  }

  logout() {
    this.authenticationService.logout();
  }

}
