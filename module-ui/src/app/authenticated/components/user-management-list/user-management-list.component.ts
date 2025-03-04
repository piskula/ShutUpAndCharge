import { ChangeDetectionStrategy, Component, computed, DestroyRef, inject, signal, viewChild } from '@angular/core';
import { AccountDTO, AccountService } from '@suac/api';
import { filter, finalize, map, Observable, switchMap, take, tap } from 'rxjs';
import {
  MatCell, MatCellDef,
  MatColumnDef,
  MatHeaderCell, MatHeaderCellDef,
  MatHeaderRow, MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable, MatTableDataSource,
} from '@angular/material/table';
import { MatSort, MatSortHeader } from '@angular/material/sort';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { NgIf, SlicePipe } from '@angular/common';
import { MatIconButton } from '@angular/material/button';
import { CdkCopyToClipboard } from '@angular/cdk/clipboard';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { SnackbarService } from '../../../common/snackbar.service';
import { PriceDialogComponent, PriceDialogData } from './price-dialog/price-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { ConfirmDialogComponent, ConfirmDialogData } from './confirm-dialog/confirm-dialog.component';
import { ResponsiveDirective } from '../../../common/responsive.directive';
import { ResponsiveService } from '../../../common/responsive.service';
import { Page, PaginatedTableComponent } from '../../../common/paginated-table/paginated-table.component';
import { TableBadgeComponent } from '../../../common/table-badge/table-badge.component';
import { AssignRfidDialogComponent, AssignRfidDialogData } from './assign-rfid-dialog/assign-rfid-dialog.component';
import { AccountDetailDialogComponent } from './account-detail-dialog/account-detail-dialog.component';
import { MatDivider } from '@angular/material/divider';

@Component({
  selector: 'app-user-management-list',
  templateUrl: './user-management-list.component.html',
  styleUrl: 'user-management-list.component.scss',
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
    MatIconButton,
    CdkCopyToClipboard,
    NgIf,
    MatMenu,
    MatMenuItem,
    MatMenuTrigger,
    MatProgressSpinner,
    ResponsiveDirective,
    PaginatedTableComponent,
    SlicePipe,
    TableBadgeComponent,
    MatDivider,
  ],
})
export class UserManagementListComponent {
  private allColumns = ['id', 'idKeycloak', 'firstName', 'assignedChipUid', 'verifiedForCharging', 'action'];
  private smallColumns = ['id', 'firstName', 'verifiedForCharging', 'action'];

  #responsiveService = inject(ResponsiveService);
  private readonly pagination = viewChild(PaginatedTableComponent);

  public displayedColumns =
    computed(() => this.#responsiveService.isMobile() ? this.smallColumns : this.allColumns);
  public dataSource = new MatTableDataSource<AccountDTO>([]);

  public isLoadingAccountId = signal<number | null>(null)

  constructor(
    private readonly accountService: AccountService,
    private readonly snackBarService: SnackbarService,
    private readonly dialog: MatDialog,
    private readonly destroyRef: DestroyRef,
  ) {
  }

  protected fetchFn = (page: number, size: number, sort: string): Observable<Page<AccountDTO>> => {
    return this.accountService.getUserList(page, size, sort)
      .pipe(map(page => page as Page<AccountDTO>));
  }

  setVerifiedFlag(
    accountId: number,
    name: String,
    flag: boolean,
  ) {
    const subtitle = flag
      ? `Are you sure, that user ${name} is allowed to charge?`
      : `Are you sure, that user ${name} is not allowed to charge anymore?`;
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: { title: 'Allowance to charge', subtitle: subtitle } as ConfirmDialogData });
    dialogRef.afterClosed().pipe(
      take(1),
      filter((yes): yes is boolean => !!yes),
      tap(() => this.isLoadingAccountId.set(accountId)),
      switchMap(() => this.accountService.updateVerifiedFlag(accountId, flag)),
      tap((isAllowed: boolean) => {
        const msg = isAllowed ? 'is allowed' : 'is not allowed';
        this.snackBarService.showInfoSnackBar(`Account of user ${name} ${msg} to charge from now on.`);
        this.pagination()?.forceRefresh();
      }),
      finalize(() => this.isLoadingAccountId.set(null)),
      takeUntilDestroyed(this.destroyRef),
    ).subscribe();
  }

  assignChipUid(
    accountId: number,
    name: String,
    assignedChipUid?: String,
  ): void {
    const dialogRef = this.dialog.open(AssignRfidDialogComponent, {
      data: { accountName: name, assignedChipUid: assignedChipUid } as AssignRfidDialogData });
    dialogRef.afterClosed().pipe(
      take(1),
      filter((newChipUid) => newChipUid != assignedChipUid),
      tap(() => this.isLoadingAccountId.set(accountId)),
      switchMap((newChipUid: string) => this.accountService.updateAssignedChipUid(accountId, newChipUid)),
      tap(() => {
        this.snackBarService.showInfoSnackBar(`You updated RFID chip for ${name}`);
        this.pagination()?.forceRefresh();
      }),
      finalize(() => this.isLoadingAccountId.set(null)),
      takeUntilDestroyed(this.destroyRef),
    ).subscribe();
  }

  topUp(accountId: number, name: string) {
    const dialogRef = this.dialog.open(PriceDialogComponent, {
      data: { accountName: name, price: 0 } as PriceDialogData,
    });
    dialogRef.afterClosed().pipe(
      take(1),
      filter((amount): amount is number => !!amount),
      tap(() => this.isLoadingAccountId.set(accountId)),
      switchMap((amount: number) => this.accountService.topUpAccount(accountId!!, amount)),
      tap((amount: number) => {
        this.snackBarService.showInfoSnackBar(`Account of user ${name} has been given credit ${amount.toFixed(2)} euro`);
      }),
      finalize(() => this.isLoadingAccountId.set(null)),
      takeUntilDestroyed(this.destroyRef),
    ).subscribe();
  }

  showDetail(account: AccountDTO): void {
    const dialogRef = this.dialog.open(AccountDetailDialogComponent, {
      data: account as AccountDTO,
    });
    dialogRef.afterClosed().pipe(
      take(1),
      takeUntilDestroyed(this.destroyRef),
    ).subscribe();
  }

}
