import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { DashboardService, TransactionFinishedDTO } from '@suac/api';
import { map, Observable } from 'rxjs';
import { Page } from '../../../common/paginated-table/paginated-table.component';
import { TransactionFinishedTableComponent } from '../transaction-finished-table/transaction-finished-table.component';

@Component({
  selector: 'app-dashboard-charging-list',
  templateUrl: './dashboard-charging-list.component.html',
  styleUrl: 'dashboard-charging-list.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [TransactionFinishedTableComponent],
})
export class DashboardChargingListComponent {
  private readonly dashboardService = inject(DashboardService);

  protected fetchFn = (page: number, size: number, sort: string): Observable<Page<TransactionFinishedDTO>> =>
    this.dashboardService.getLastChargings(page, size, sort)
      .pipe(map(page => page as Page<TransactionFinishedDTO>));
}
