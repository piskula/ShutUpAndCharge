import { ChangeDetectionStrategy, Component, computed } from '@angular/core';
import { MatToolbar } from '@angular/material/toolbar';
import { MatIcon } from '@angular/material/icon';
import { MatAnchor, MatButton, MatIconButton } from '@angular/material/button';
import { MatChip, MatChipSet } from '@angular/material/chips';
import { MatTooltip } from '@angular/material/tooltip';
import { MatMenu, MatMenuTrigger } from '@angular/material/menu';
import { MatDivider } from '@angular/material/divider';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthenticationService } from '../../../security/authentication.service';

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
    MatTooltip,
    MatMenu,
    MatMenuTrigger,
    MatButton,
    MatDivider,
    RouterLinkActive,
    RouterLink,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HeaderMobileComponent {

  public readonly currentUser = computed(() => this.authenticationService.currentUserValue());

  constructor(
    private readonly authenticationService: AuthenticationService,
  ) {
  }

  logout() {
    this.authenticationService.logout();
  }

}
