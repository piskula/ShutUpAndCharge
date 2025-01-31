import { ChangeDetectionStrategy, Component } from '@angular/core';
import { DatePipe } from '@angular/common';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatSortModule, Sort } from '@angular/material/sort';
import { map, Observable } from 'rxjs';
import { TemporaryTransactionService, TransactionTemporaryDTO } from '@suac/api';
import { Page, PaginatedTableComponent } from '../../../common/paginated-table/paginated-table.component';

@Component({
  selector: 'app-transaction-temporary-list',
  templateUrl: './transaction-temporary-list.component.html',
  styleUrl: 'transaction-temporary-list.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    MatTableModule,
    MatSortModule,
    PaginatedTableComponent,
    DatePipe,
  ],
})
export class TransactionTemporaryListComponent {
  protected readonly displayedColumns = ['timeStartUtc', 'trxNumber', 'trxIdentifier', 'energyMeter', 'accountName'];
  protected dataSource = new MatTableDataSource<TransactionTemporaryDTO>([]);

  protected readonly defaultSort: Sort = {
    active: 'timeStartUtc',
    direction: 'desc',
  };

  constructor(
    private readonly transactionService: TemporaryTransactionService,
  ) {
  }

  protected fetchFn = (page: number, size: number, sort: string): Observable<Page<TransactionTemporaryDTO>> => {
    return this.transactionService.getList(page, size, sort)
      .pipe(map(page => page as Page<TransactionTemporaryDTO>));
  }

}
