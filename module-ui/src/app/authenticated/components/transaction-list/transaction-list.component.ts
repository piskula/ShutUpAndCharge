import { ChangeDetectionStrategy, Component, computed, OnInit, signal, viewChild } from '@angular/core';
import { ChargingListDTO, FinishedTransactionService } from '@suac/api';
import { map, Observable } from 'rxjs';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable,
  MatTableDataSource,
} from '@angular/material/table';
import { MatSort, MatSortHeader, Sort } from '@angular/material/sort';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { CurrencyPipe, DatePipe, DecimalPipe, NgIf } from '@angular/common';
import { Page, PaginatedTableComponent } from '../../../common/paginated-table/paginated-table.component';
import {
  MatAccordion,
  MatExpansionPanel, MatExpansionPanelDescription,
  MatExpansionPanelHeader,
  MatExpansionPanelTitle,
} from '@angular/material/expansion';
import {
  MatChipAvatar,
  MatChipRemove,
  MatChipRow, MatChipSet,
} from '@angular/material/chips';
import { AuthenticationService, CurrentUser } from '../../../security/authentication.service';
import { MatButton } from '@angular/material/button';

export interface TransactionFilter {
  id: string;
  field: string;
  values: number[];
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
    MatTable,
    MatSort,
    MatSortHeader,
    MatHeaderCell,
    MatCell,
    MatTooltip,
    DatePipe,
    MatIcon,
    DecimalPipe,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRowDef,
    MatHeaderCellDef,
    MatCellDef,
    MatRow,
    MatColumnDef,
    NgIf,
    CurrencyPipe,
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatAccordion,
    MatExpansionPanelTitle,
    MatExpansionPanelDescription,
    MatChipRemove,
    MatChipRow,
    MatChipAvatar,
    MatChipSet,
    MatButton,
  ],
})
export class TransactionListComponent implements OnInit {
  protected readonly displayedColumns = ['time', 'account', 'stationId', 'kwh', 'price'];
  protected dataSource = new MatTableDataSource<ChargingListDTO>([]);

  protected readonly defaultSort: Sort = {
    active: 'time',
    direction: 'desc',
  };
  private readonly pagination = viewChild(PaginatedTableComponent);

  readonly activeFilters = signal<TransactionFilter[]>([]);
  readonly isAdmin = computed(() => this.authService.currentUserValue()?.roles?.includes('Admin') ?? false);
  currentUser: CurrentUser | null;

  constructor(
    private readonly transactionService: FinishedTransactionService,
    private readonly authService: AuthenticationService,
  ) {
    this.currentUser = this.authService.currentUserValue();
  }

  ngOnInit(): void {
    this.addCurrentUserFilter();
  }

  protected fetchFn = (page: number, size: number, sort: string): Observable<Page<ChargingListDTO>> => {
    return this.transactionService.getList({ accountIds: this.activeFilters().find(f => f.field === 'accountIds')?.values }, page, size, sort)
      .pipe(map(page => page as Page<ChargingListDTO>));
  }

  removeFilter(filterId: string): void {
    this.activeFilters.update(filters => filters.filter(filter => filter.id !== filterId));
    this.pagination()?.forceRefresh();
  }

  addCurrentUserFilter() {
    if (!this.currentUser) {
      return;
    }

    const currentUserFilter = {
        id: `current-user-${this.currentUser?.id}`,
        field: 'accountIds',
        values: [this.currentUser.id],
        title: this.currentUser.name,
        icon: 'person',
      };

    this.activeFilters.update(filters => {
      if (filters.every(filter => filter.field !== 'accountIds')) {
        return [...filters, currentUserFilter]
      } else {
        return filters
      }
    });
    this.pagination()?.forceRefresh();
    // TODO when refreshing always close filter expansion panel, also trigger refresh on activeFilters change, not manually
  }

}
