import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { DashboardService, TransactionFinishedDTO } from '@suac/api';
import { map, Observable } from 'rxjs';
import {
  MatCell, MatCellDef,
  MatColumnDef,
  MatHeaderCell, MatHeaderCellDef,
  MatHeaderRow, MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable, MatTableDataSource,
} from '@angular/material/table';
import { MatSort, MatSortHeader, Sort } from '@angular/material/sort';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { CurrencyPipe, DatePipe, DecimalPipe } from '@angular/common';
import { ResponsiveService } from '../../../common/responsive.service';
import { Page, PaginatedTableComponent } from '../../../common/paginated-table/paginated-table.component';
import { PriceColoredComponent } from '../../../common/price-colored/price-colored.component';

@Component({
  selector: 'app-dashboard-charging-list',
  templateUrl: './dashboard-charging-list.component.html',
  styleUrl: 'dashboard-charging-list.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    MatTable,
    MatSort,
    MatSortHeader,
    MatHeaderCell,
    MatCell,
    MatIcon,
    MatTooltip,
    MatColumnDef,
    MatHeaderRow,
    MatRow,
    MatRowDef,
    MatHeaderRowDef,
    MatHeaderCellDef,
    MatCellDef,
    DatePipe,
    DecimalPipe,
    PaginatedTableComponent,
    CurrencyPipe,
    PriceColoredComponent,
  ],
})
export class DashboardChargingListComponent {
  private readonly columnsAll = ['timeStartUtc', 'stationId', 'kwh', 'price'];
  private readonly columnsSmall = ['timeStartUtc', 'kwh', 'price'];

  protected readonly defaultSort: Sort = {
    active: 'timeStartUtc',
    direction: 'desc',
  };

  private readonly responsiveService = inject(ResponsiveService);

  protected displayedColumns =
    computed(() => this.responsiveService.isMobile() ? this.columnsSmall : this.columnsAll);
  protected dataSource = new MatTableDataSource<TransactionFinishedDTO>([]);

  constructor(
    private readonly dashboardService: DashboardService,
  ) {
  }

  protected fetchFn = (page: number, size: number, sort: string): Observable<Page<TransactionFinishedDTO>> => {
    return this.dashboardService.getLastChargings(page, size, sort)
      .pipe(map(page => page as Page<TransactionFinishedDTO>));
  }

}
