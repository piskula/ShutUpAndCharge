import { ChangeDetectionStrategy, Component, computed, effect, inject, signal, untracked, viewChild } from '@angular/core';
import { FinishedTransactionService, TransactionFilterDTO, TransactionFinishedDTO } from '@suac/api';
import { map, Observable } from 'rxjs';
import { Page } from '../../../common/paginated-table/paginated-table.component';
import { AuthenticationService } from '../../../security/authentication.service';
import { MatExpansionModule, MatExpansionPanelHeader } from '@angular/material/expansion';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { TransactionFinishedTableComponent } from '../transaction-finished-table/transaction-finished-table.component';
import { TransactionKwhChartComponent } from '../transaction-kwh-chart/transaction-kwh-chart.component';
import { HttpResponse } from '@angular/common/http';

export type TransactionType = 'all' | 'credit' | 'charging';

export interface ActiveFilterChip {
  id: 'mine' | 'type';
  title: string;
  icon: string;
  filled?: boolean;
}

@Component({
  selector: 'app-transaction-list',
  templateUrl: './transaction-list.component.html',
  styleUrl: 'transaction-list.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    MatExpansionModule,
    MatIconModule,
    MatChipsModule,
    MatSlideToggleModule,
    MatButtonToggleModule,
    TransactionFinishedTableComponent,
    TransactionKwhChartComponent,
  ],
})
export class TransactionListComponent {
  protected authService = inject(AuthenticationService);
  #transactionService = inject(FinishedTransactionService);
  private readonly table = viewChild(TransactionFinishedTableComponent);
  private readonly panelHeader = viewChild(MatExpansionPanelHeader);

  protected readonly onlyMine = signal(false);
  protected readonly transactionType = signal<TransactionType>('all');
  protected readonly panelExpanded = signal(false);

  // Same filter as used by the table; depends only on signals, so the chart reloads whenever the table is refreshed.
  protected readonly chartFilter = computed(() => this.buildFilter());

  protected readonly activeFilters = computed<ActiveFilterChip[]>(() => {
    const filters: ActiveFilterChip[] = [];
    if (this.onlyMine()) {
      filters.push({ id: 'mine', title: 'Mine', icon: 'person' });
    }
    switch (this.transactionType()) {
      case 'credit':
        filters.push({ id: 'type', title: 'Top-ups', icon: 'money_bag' });
        break;
      case 'charging':
        filters.push({ id: 'type', title: 'Charging', icon: 'power', filled: true });
        break;
    }
    return filters;
  });

  constructor() {
    // Enable "Only mine" by default, once, as soon as the current user becomes available.
    const defaultApplied = effect(() => {
      if (this.authService.currentUserValue()) {
        untracked(() => this.setOnlyMine(true));
        defaultApplied.destroy();
      }
    });
  }

  protected setOnlyMine(value: boolean): void {
    if (this.onlyMine() === value) return;
    this.onlyMine.set(value);
    this.table()?.resetAndRefresh();
  }

  protected setTransactionType(value: TransactionType | null): void {
    const type = value ?? 'all';
    if (this.transactionType() === type) return;
    this.transactionType.set(type);
    this.table()?.resetAndRefresh();
  }

  protected removeFilter(id: ActiveFilterChip['id']): void {
    if (id === 'mine') {
      this.setOnlyMine(false);
    } else {
      this.setTransactionType('all');
    }
    // The removed chip had focus; hand it over to the panel header so keyboard focus isn't lost.
    this.panelHeader()?.focus();
  }

  private buildFilter(): TransactionFilterDTO {
    const filter: TransactionFilterDTO = {};
    const user = this.authService.currentUserValue();
    if (this.onlyMine() && user) {
      filter.accountIds = [user.id];
    }
    switch (this.transactionType()) {
      case 'credit':
        filter.onlyCredit = true;
        break;
      case 'charging':
        filter.onlyCredit = false;
        break;
    }
    return filter;
  }

  protected fetchFn = (page: number, size: number, sort: string): Observable<Page<TransactionFinishedDTO>> =>
    this.#transactionService.getList1(this.buildFilter(), page, size, sort)
      .pipe(map(page => page as Page<TransactionFinishedDTO>));

  protected exportFn = (sort: string): Observable<HttpResponse<Blob>> =>
    this.#transactionService.exportTransactions(this.buildFilter(), sort, 'response');
}
