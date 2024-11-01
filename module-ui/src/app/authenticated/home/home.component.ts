import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { CurrentUserService, CurrentUserDTO } from '@suac/api';
import { tap } from 'rxjs';
import { MatIcon } from "@angular/material/icon";
import { MatAnchor, MatButton, MatIconButton } from "@angular/material/button";
import { MatToolbar } from "@angular/material/toolbar";
import { FooterComponent } from "../../common/footer/footer.component";
import { DashboardChargingListComponent } from './dashboard-charging-list/dashboard-charging-list.component';
import { MatChip, MatChipSet } from '@angular/material/chips';
import { NgForOf } from '@angular/common';
import { ChargingStatusComponent } from '../../common/charging-status/charging-status.component';

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
  ],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomeComponent implements OnInit {
  public user = signal('');
  public roles = signal<string[]>([]);

  constructor(
    private currentUserService: CurrentUserService,
  ) {
  }

  ngOnInit(): void {
    this.currentUserService.getCurrentUser().pipe(
      tap((user: CurrentUserDTO) => {
        this.user.set(`${user.firstName} ${user.lastName}`);
        const rolesEnum: Array<CurrentUserDTO.RolesEnum> = user.roles!!;
        this.roles.set(rolesEnum.map((r: CurrentUserDTO.RolesEnum) => r.toString()));
      }),
    ).subscribe();
  }

}
