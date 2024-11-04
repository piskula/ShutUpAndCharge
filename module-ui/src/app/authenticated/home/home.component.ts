import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { CurrentUserService, CurrentUserDTO } from '@suac/api';
import { take, tap } from 'rxjs';
import { MatIcon } from "@angular/material/icon";
import { MatAnchor, MatButton, MatIconButton } from "@angular/material/button";
import { MatToolbar } from "@angular/material/toolbar";
import { FooterComponent } from "../../common/footer/footer.component";
import { DashboardChargingListComponent } from './dashboard-charging-list/dashboard-charging-list.component';
import { MatChip, MatChipSet } from '@angular/material/chips';
import { NgForOf, NgIf } from '@angular/common';
import { ChargingStatusComponent } from '../../common/charging-status/charging-status.component';
import { UserManagementListComponent } from './user-management-list/user-management-list.component';
import { HeaderComponent } from '../../common/header/header.component';
import { HeaderData } from '../../common/header/model/header-data';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: 'home.component.scss',
  imports: [
    MatIcon,
    MatIconButton,
    MatToolbar,
    MatAnchor,
    MatButton,
    FooterComponent,
    DashboardChargingListComponent,
    MatChipSet,
    MatChip,
    NgForOf,
    ChargingStatusComponent,
    UserManagementListComponent,
    NgIf,
    HeaderComponent,
  ],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomeComponent implements OnInit {
  public headerData = signal<HeaderData>({ user: '', roles: []});

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
