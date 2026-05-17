import { ChangeDetectionStrategy, Component, inject, OnInit, signal, viewChild } from '@angular/core';
import { FinishedTransactionService, TransactionFinishedDTO } from '@suac/api';
import { map, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Page } from '../../../common/paginated-table/paginated-table.component';
import { AuthenticationService, CurrentUser } from '../../../security/authentication.service';
import { MatAccordion, MatExpansionModule } from '@angular/material/expansion';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { TransactionFinishedTableComponent } from '../transaction-finished-table/transaction-finished-table.component';

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
    MatAccordion,
    MatExpansionModule,
    MatIconModule,
    MatChipsModule,
    MatButtonModule,
    TransactionFinishedTableComponent,
  ],
})
export class TransactionListComponent implements OnInit {
  #transactionService = inject(FinishedTransactionService);
  #http = inject(HttpClient);
  protected authService = inject(AuthenticationService);
  private readonly table = viewChild(TransactionFinishedTableComponent);

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

  protected exportFn = (page: number, size: number, sort: string): Observable<Blob> =>
    this.#http.post('/api/transaction/finished/export',
      { accountIds: this.activeFilters().find(f => f.field === 'accountIds')?.values },
      { params: { page, size, sort }, responseType: 'blob' },
    );

  removeFilter(filterId: string): void {
    this.activeFilters.update(filters => filters.filter(filter => filter.id !== filterId));
    this.table()?.forceRefresh();
  }

  addCurrentUserFilter() {
    if (!this.currentUser) return;

    const currentUserFilter: TransactionFilter = {
      id: `current-user-${this.currentUser.id}`,
      field: 'accountIds',
      values: [this.currentUser.id],
      title: this.currentUser.name,
      icon: 'person',
    };

    this.activeFilters.update(filters => {
      if (filters.every(filter => filter.field !== 'accountIds')) {
        return [...filters, currentUserFilter];
      }
      return filters;
    });
    this.table()?.forceRefresh();
  }
}
