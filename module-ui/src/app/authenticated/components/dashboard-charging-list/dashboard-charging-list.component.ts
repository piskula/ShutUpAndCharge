import { ChangeDetectionStrategy, Component, computed, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { ChargingService, ChargingListDTO, PageDTOChargingListDTO } from '@suac/api';
import { combineLatest, debounceTime, switchMap, tap } from 'rxjs';
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
import { DatePipe, DecimalPipe, NgIf } from '@angular/common';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { ResponsiveService } from '../../../common/responsive.service';
import { MatPaginator, PageEvent } from '@angular/material/paginator';

@Component({
  selector: 'app-dashboard-charging-list',
  templateUrl: './dashboard-charging-list.component.html',
  styleUrl: 'dashboard-charging-list.component.scss',
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
    DecimalPipe,
    NgIf,
    MatPaginator,
  ],
})
export class DashboardChargingListComponent implements OnInit {
  private readonly columnsAll = ['time', 'stationId', 'kwh', 'price'];
  private readonly columnsSmall = ['time', 'kwh', 'price'];

  private readonly responsiveService = inject(ResponsiveService);

  public displayedColumns =
    computed(() => this.responsiveService.isMobile() ? this.columnsSmall : this.columnsAll);
  public dataSource = new MatTableDataSource<ChargingListDTO>([]);
  public total = signal(0);

  public readonly pageSizeOptions = [5, 10, 25, 100];
  private readonly defaultSort: {
    sortActive: string;
    sortDirection: "asc" | "desc" | "";
  } = {
    sortActive: "time",
    sortDirection: "desc",
  };
  public readonly pageIndex = signal(0);
  public readonly pageSize = signal(this.pageSizeOptions[0]);
  public readonly sort = signal(this.defaultSort);
  private readonly sortString = computed(() => `${this.sort().sortActive},${this.sort().sortDirection}`);
  private readonly onPageChange$ = combineLatest([
    toObservable(this.pageIndex),
    toObservable(this.pageSize),
    toObservable(this.sortString),
  ]);

  constructor(
    private chargingService: ChargingService,
    private destroyRef: DestroyRef,
  ) {
  }

  ngOnInit(): void {
    this.onPageChange$.pipe(
      debounceTime(1), // changing sort and switching to first page would still emit value twice
      switchMap(([page, size, sortString]) => this.chargingService.getChargingList(page, size, sortString)),
      tap((page: PageDTOChargingListDTO) => {
        this.dataSource.data = page.content as ChargingListDTO[];
        this.total.set(page.totalElements ?? 0);
      }),
      takeUntilDestroyed(this.destroyRef),
    ).subscribe();
  }

  onSortChange(sort: Sort) {
    this.sort.set({ sortActive: sort.active, sortDirection: sort.direction });
    this.pageIndex.set(0);
  }

  pageChanged(pageEvent: PageEvent) {
    this.pageIndex.set(pageEvent.pageIndex);
    this.pageSize.set(pageEvent.pageSize);
  }

}
