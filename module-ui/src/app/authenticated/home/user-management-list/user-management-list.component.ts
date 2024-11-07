import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { AccountDTO, AccountService, PageDTOAccountDTO } from '@suac/api';
import { BehaviorSubject, map, switchMap, tap } from 'rxjs';
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
import { DatePipe, NgIf } from '@angular/common';
import { MatIconButton } from '@angular/material/button';
import { CdkCopyToClipboard } from '@angular/cdk/clipboard';

@Component({
  selector: 'app-user-management-list',
  templateUrl: './user-management-list.component.html',
  styleUrl: 'user-management-list.component.scss',
  standalone: true,
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
    MatIconButton,
    CdkCopyToClipboard,
    NgIf,
  ],
})
export class UserManagementListComponent implements OnInit {

  public displayedColumns = ['id', /*'idKeycloak',*/ 'firstName', 'verifiedForCharging'];
  public dataSource = new MatTableDataSource<AccountDTO>([]);

  public sort$ = new BehaviorSubject<{
    sortActive: string;
    sortDirection: "asc" | "desc" | "";
  }>({
    sortActive: "id",
    sortDirection: "desc",
  });

  constructor(
    private accountService: AccountService,
  ) {
  }

  ngOnInit(): void {
    this.sort$.pipe(
      switchMap(sort => this.accountService.getUserList(0, 20, `${sort.sortActive},${sort.sortDirection}`)),
      map((page: PageDTOAccountDTO) => page.content || []),
      tap((list: AccountDTO[]) => this.dataSource.data = list),
    ).subscribe();
  }

  onSortChange(sort: Sort) {
    this.sort$.next({ sortActive: sort.active, sortDirection: sort.direction });
  }

}