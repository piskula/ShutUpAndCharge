import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { CurrentUserService, CurrentUserDTO } from '@suac/api';
import { MatToolbar } from '@angular/material/toolbar';
import { MatIcon } from '@angular/material/icon';
import { NgForOf, NgIf } from '@angular/common';
import { MatAnchor, MatFabAnchor, MatIconButton } from '@angular/material/button';
import { MatChip, MatChipSet } from '@angular/material/chips';
import { HeaderData } from '../model/header-data';
import { take, tap } from 'rxjs';
import { MatTooltip } from '@angular/material/tooltip';

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
  ],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HeaderMobileComponent implements OnInit {

  public headerData = signal<HeaderData | null>(null);

  constructor(
    private currentUserService: CurrentUserService,
  ) {
  }

  ngOnInit(): void {
    this.currentUserService.getCurrentUser().pipe(
      take(1),
      tap((user: CurrentUserDTO) => {
        const rolesEnum: Array<CurrentUserDTO.RolesEnum> = user.roles!!;
        this.headerData.set({
          user: `${user.firstName} ${user.lastName}`,
          roles: rolesEnum.map((r: CurrentUserDTO.RolesEnum) => r.toString()),
        });
      }),
    ).subscribe();
  }

}
