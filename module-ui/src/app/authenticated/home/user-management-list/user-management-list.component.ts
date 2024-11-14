import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, signal } from '@angular/core';
import { AccountDTO, AccountService, PageDTOAccountDTO, ChargingService } from '@suac/api';
import { BehaviorSubject, finalize, map, switchMap, take, tap } from 'rxjs';
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
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { SnackbarService } from '../../../common/snackbar.service';
import { PriceDialogComponent, PriceDialogData } from './price-dialog/price-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatProgressSpinner } from '@angular/material/progress-spinner';

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
    MatMenu,
    MatMenuItem,
    MatMenuTrigger,
    MatProgressSpinner,
  ],
})
export class UserManagementListComponent implements OnInit {

  public isLoadingAccountId = signal<number | null>(null)

  public displayedColumns = ['id', /*'idKeycloak',*/ 'firstName', 'verifiedForCharging', 'action'];
  public dataSource = new MatTableDataSource<AccountDTO>([]);

  public sort$ = new BehaviorSubject<{
    sortActive: string;
    sortDirection: "asc" | "desc" | "";
  }>({
    sortActive: "id",
    sortDirection: "desc",
  });

  constructor(
    private readonly accountService: AccountService,
    private readonly chargingService: ChargingService,
    private readonly snackBarService: SnackbarService,
    private readonly dialog: MatDialog,
    private readonly destroyRef: DestroyRef,
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

  setVerifiedForCharging(accountId: number) {
    this.snackBarService.showInfoSnackBar('TODO set verified ' + accountId);
  }

  unsetVerifiedForCharging(accountId: number) {
    this.snackBarService.showInfoSnackBar('TODO unset verified ' + accountId);
  }

  topUp(account: AccountDTO) {
    const dialogRef = this.dialog.open(PriceDialogComponent, {
      data: { accountName: `${account.firstName} ${account.lastName}`, price: 0 } as PriceDialogData,
    });
    dialogRef.afterClosed().pipe(
      take(1),
      tap(() => this.isLoadingAccountId.set(account.id!!)),
      switchMap((amount: number) => this.chargingService.topUpAccount(account.id!!, amount)),
      tap((amount: number) => {
        this.snackBarService.showInfoSnackBar(`Account of user ${account.firstName} ${account.lastName} has been given credit ${amount.toFixed(2)} euro`);
      }),
      finalize(() => this.isLoadingAccountId.set(null)),
      takeUntilDestroyed(this.destroyRef),
    ).subscribe();
  }

}
