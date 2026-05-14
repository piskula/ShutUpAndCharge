import { ChangeDetectionStrategy, Component, computed } from '@angular/core';
import { ChargingStatusComponent } from '../../common/charging-status/charging-status.component';
import {
  DashboardChargingListComponent
} from '../components/dashboard-charging-list/dashboard-charging-list.component';
import { MonthlyOverviewComponent } from '../components/monthly-overview/monthly-overview.component';
import { AuthenticationService } from '../../security/authentication.service';
import { MyBalanceComponent } from '../../common/my-balance/my-balance.component';

@Component({
  selector: 'app-home-dashboard',
  templateUrl: './home-dashboard.component.html',
  styleUrl: 'home-dashboard.component.scss',
  imports: [
    ChargingStatusComponent,
    DashboardChargingListComponent,
    MonthlyOverviewComponent,
    MyBalanceComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomeDashboardComponent {

  public headerData = computed(() => {
    const user = this.authenticationService.currentUserValue();
    if (!user) return null;

    return {
      user: user.name,
      roles: user.roles,
    };
  })

  constructor(
    private readonly authenticationService: AuthenticationService,
  ) {
  }

}
