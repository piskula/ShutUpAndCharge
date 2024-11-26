import { ChangeDetectionStrategy, Component } from '@angular/core';
import { TransactionService, ChargingListDTO } from '@suac/api';
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
import { DatePipe, DecimalPipe, NgIf } from '@angular/common';
import { Page, PaginatedTableComponent } from '../../../common/paginated-table/paginated-table.component';

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
    MatHeaderCellDef,
    MatCellDef,
    DatePipe,
    MatIcon,
    DecimalPipe,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRowDef,
    MatRow,
    MatColumnDef,
    NgIf,
  ],
})
export class TransactionListComponent {
  protected readonly displayedColumns = ['time', 'account', 'stationId', 'kwh', 'price'];
  protected dataSource = new MatTableDataSource<ChargingListDTO>([]);

  protected readonly defaultSort: Sort = {
    active: "time",
    direction: "desc",
  };

  constructor(
    private readonly transactionService: TransactionService,
  ) {
  }

  protected fetchFn = (page: number, size: number, sort: string): Observable<Page<ChargingListDTO>> => {
    return this.transactionService.getList(page, size, sort)
      .pipe(map(page => page as Page<ChargingListDTO>));
  }

}
