import { ChangeDetectionStrategy, Component, computed, inject, input, viewChild } from '@angular/core';
import { TransactionFinishedDTO } from '@suac/api';
import { Observable } from 'rxjs';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatSortModule, Sort } from '@angular/material/sort';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { DatePipe, DecimalPipe } from '@angular/common';
import { Page, PaginatedTableComponent } from '../../../common/paginated-table/paginated-table.component';
import { ResponsiveService } from '../../../common/responsive.service';
import { PriceColoredComponent } from '../../../common/price-colored/price-colored.component';
import { ResponsiveDirective } from '../../../common/responsive.directive';

@Component({
  selector: 'app-transaction-finished-table',
  templateUrl: './transaction-finished-table.component.html',
  styleUrl: './transaction-finished-table.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    PaginatedTableComponent,
    MatTableModule,
    MatSortModule,
    MatIconModule,
    MatTooltipModule,
    DatePipe,
    DecimalPipe,
    PriceColoredComponent,
    ResponsiveDirective,
  ],
})
export class TransactionFinishedTableComponent {
  readonly fetchFn = input.required<(page: number, size: number, sort: string) => Observable<Page<TransactionFinishedDTO>>>();
  readonly exportFn = input<((page: number, size: number, sort: string) => Observable<Blob>) | undefined>(undefined);
  readonly defaultPageSize = input<number>(10);
  readonly hideAccountColumn = input<boolean>(false);
  readonly hideStatsColumn = input<boolean>(false);

  private readonly responsiveService = inject(ResponsiveService);
  private readonly paginatedTable = viewChild.required(PaginatedTableComponent);

  protected readonly defaultSort: Sort = { active: 'timeStartUtc', direction: 'desc' };
  protected readonly dataSource = new MatTableDataSource<TransactionFinishedDTO>([]);

  protected readonly displayedColumns = computed(() => {
    const isMobile = this.responsiveService.isMobile();
    const cols = ['timeStartUtc'];
    if (!this.hideAccountColumn()) cols.push('account');
    if (!isMobile) cols.push('stationId');
    cols.push('kwh', 'price');
    if (!this.hideStatsColumn()) cols.push('stats');
    return cols;
  });

  public forceRefresh(): void {
    this.paginatedTable().forceRefresh();
  }
}
