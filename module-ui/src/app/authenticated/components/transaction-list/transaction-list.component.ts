import { ChangeDetectionStrategy, Component, computed, inject, OnInit, signal, viewChild } from '@angular/core';
import { FinishedTransactionService, TransactionFinishedDTO } from '@suac/api';
import { map, Observable } from 'rxjs';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatSortModule, Sort } from '@angular/material/sort';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { DatePipe, DecimalPipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Page, PaginatedTableComponent } from '../../../common/paginated-table/paginated-table.component';
import { AuthenticationService, CurrentUser } from '../../../security/authentication.service';
import { ResponsiveService } from '../../../common/responsive.service';
import { MatAccordion, MatExpansionModule } from '@angular/material/expansion';
import { MatChipsModule } from '@angular/material/chips';
import { ResponsiveDirective } from '../../../common/responsive.directive';
import { PriceColoredComponent } from '../../../common/price-colored/price-colored.component';

export interface TransactionFilter {
  id: string;
  field: string;
  values: string[];
  title: string;
  icon: string;
}

@Component({
  selector: 'app-transaction-list',
  templateUrl: './transaction-list.component.html',
  styleUrl: 'transaction-list.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    PaginatedTableComponent,
    MatAccordion,
    MatExpansionModule,
    MatIconModule,
    MatChipsModule,
    MatTableModule,
    MatSortModule,
    MatTooltipModule,
    DatePipe,
    DecimalPipe,
    ResponsiveDirective,
    PriceColoredComponent,
  ],
})
export class TransactionListComponent implements OnInit {
  private readonly allColumns = ['timeStartUtc', 'account', 'stationId', 'kwh', 'price', 'stats'];
  private readonly smallColumns = ['timeStartUtc', 'account', 'kwh', 'price', 'stats'];

  #responsiveService = inject(ResponsiveService);
  #transactionService = inject(FinishedTransactionService);
  #http = inject(HttpClient);
  protected authService = inject(AuthenticationService);
  private readonly pagination = viewChild(PaginatedTableComponent);

  public displayedColumns =
    computed(() => this.#responsiveService.isMobile() ? this.smallColumns : this.allColumns);
  protected dataSource = new MatTableDataSource<TransactionFinishedDTO>([]);

  protected readonly defaultSort: Sort = {
    active: 'timeStartUtc',
    direction: 'desc',
  };

  readonly activeFilters = signal<TransactionFilter[]>([]);
  currentUser: CurrentUser | null;

  constructor() {
    this.currentUser = this.authService.currentUserValue();
  }

  ngOnInit(): void {
    this.addCurrentUserFilter();
  }

  protected fetchFn = (page: number, size: number, sort: string): Observable<Page<TransactionFinishedDTO>> =>
    this.#transactionService.getList1({ accountIds: this.activeFilters().find(f => f.field === 'accountIds')?.values }, page, size, sort)
      .pipe(map(page => page as Page<TransactionFinishedDTO>));

  // TODO use more generic way and handle file name from response
  protected exportFn = (page: number, size: number, sort: string): Observable<Blob> =>
    this.#http.post('/api/transaction/finished/export',
      { accountIds: this.activeFilters().find(f => f.field === 'accountIds')?.values },
      { params: { page, size, sort }, responseType: 'blob' },
    );

  removeFilter(filterId: string): void {
    this.activeFilters.update(filters => filters.filter(filter => filter.id !== filterId));
    this.pagination()?.forceRefresh();
  }

  addCurrentUserFilter() {
    if (!this.currentUser) {
      return;
    }

    const currentUserFilter: TransactionFilter = {
        id: `current-user-${this.currentUser?.id}`,
        field: 'accountIds',
        values: [this.currentUser.id],
        title: this.currentUser.name,
        icon: 'person',
      };

    this.activeFilters.update(filters => {
      if (filters.every(filter => filter.field !== 'accountIds')) {
        return [...filters, currentUserFilter];
      } else {
        return filters;
      }
    });
    this.pagination()?.forceRefresh();
    // TODO when refreshing always close filter expansion panel, also trigger refresh on activeFilters change, not manually
  }

}
