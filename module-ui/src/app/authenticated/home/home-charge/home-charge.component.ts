import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { CurrentUserService, CurrentUserDTO } from '@suac/api';
import { take, tap } from 'rxjs';
import { HeaderData } from '../../../common/header/model/header-data';
import { ChargingStatusComponent } from '../../../common/charging-status/charging-status.component';
import { DashboardChargingListComponent } from '../dashboard-charging-list/dashboard-charging-list.component';

@Component({
  selector: 'app-home-charge',
  templateUrl: './home-charge.component.html',
  styleUrl: 'home-charge.component.scss',
  imports: [
    ChargingStatusComponent,
    DashboardChargingListComponent,
  ],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomeChargeComponent implements OnInit {
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
