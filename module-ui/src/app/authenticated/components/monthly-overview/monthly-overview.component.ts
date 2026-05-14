import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { DashboardService, MonthlyTransactionSummaryDTO } from '@suac/api';
import { map, Observable } from 'rxjs';
import { ResponsiveService } from '../../../common/responsive.service';
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
import { DatePipe } from '@angular/common';
import { Page, PaginatedTableComponent } from '../../../common/paginated-table/paginated-table.component';
import { PriceColoredComponent } from '../../../common/price-colored/price-colored.component';

@Component({
  selector: 'app-monthly-overview',
  templateUrl: './monthly-overview.component.html',
  styleUrl: 'monthly-overview.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    MatTable,
    MatSort,
    MatSortHeader,
    MatHeaderCell,
    MatCell,
    MatColumnDef,
    MatHeaderRow,
    MatRow,
    MatRowDef,
    MatHeaderRowDef,
    MatHeaderCellDef,
    MatCellDef,
    DatePipe,
    PaginatedTableComponent,
    PriceColoredComponent,
  ],
})
export class MonthlyOverviewComponent {
  private readonly columnsAll = ['month', 'negativeCount', 'negativeSum', 'positiveCount', 'positiveSum', 'totalSum'];
  private readonly columnsMobile = ['month', 'negativeSum', 'positiveSum', 'totalSum'];

  private readonly responsiveService = inject(ResponsiveService);
  protected readonly displayedColumns = computed(() =>
    this.responsiveService.isMobile() ? this.columnsMobile : this.columnsAll
  );

  protected dataSource = new MatTableDataSource<MonthlyTransactionSummaryDTO>([]);
  protected readonly defaultSort: Sort = { active: 'month', direction: 'desc' };

  constructor(private readonly dashboardService: DashboardService) {}

  protected fetchFn = (page: number, size: number, sort: string): Observable<Page<MonthlyTransactionSummaryDTO>> =>
    this.dashboardService.getMonthlySummary(page, size, sort)
      .pipe(map(p => p as Page<MonthlyTransactionSummaryDTO>));
}
