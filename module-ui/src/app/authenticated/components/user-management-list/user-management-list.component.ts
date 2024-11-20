import { ChangeDetectionStrategy, Component, computed, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { AccountDTO, AccountService, PageDTOAccountDTO, ChargingService } from '@suac/api';
import { BehaviorSubject, combineLatest, filter, finalize, map, switchMap, take, tap } from 'rxjs';
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
import { ConfirmDialogComponent, ConfirmDialogData } from './confirm-dialog/confirm-dialog.component';
import { ResponsiveDirective } from '../../../common/responsive.directive';
import { ResponsiveService } from '../../../common/responsive.service';

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
    ResponsiveDirective,
  ],
})
export class UserManagementListComponent implements OnInit {
  private allColumns = ['id', 'idKeycloak', 'firstName', 'verifiedForCharging', 'action'];
  private smallColumns = ['id', 'firstName', 'verifiedForCharging', 'action'];

  private readonly responsiveService = inject(ResponsiveService);

  public displayedColumns =
    computed(() => this.responsiveService.isMobile() ? this.smallColumns : this.allColumns);
  public dataSource = new MatTableDataSource<AccountDTO>([]);

  public isLoadingAccountId = signal<number | null>(null)

  public refresh$ = new BehaviorSubject(true);
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
    combineLatest([
      this.sort$,
      this.refresh$,
    ]).pipe(
      switchMap(([sort]) => this.accountService.getUserList(0, 20, `${sort.sortActive},${sort.sortDirection}`)),
      map((page: PageDTOAccountDTO) => page.content || []),
      tap((list: AccountDTO[]) => this.dataSource.data = list),
    ).subscribe();
  }

  onSortChange(sort: Sort) {
    this.sort$.next({ sortActive: sort.active, sortDirection: sort.direction });
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
        this.refresh$.next(true);
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
      switchMap((amount: number) => this.chargingService.topUpAccount(accountId!!, amount)),
      tap((amount: number) => {
        this.snackBarService.showInfoSnackBar(`Account of user ${name} has been given credit ${amount.toFixed(2)} euro`);
      }),
      finalize(() => this.isLoadingAccountId.set(null)),
      takeUntilDestroyed(this.destroyRef),
    ).subscribe();
  }

}
