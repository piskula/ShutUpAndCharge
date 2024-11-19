import { ChangeDetectionStrategy, Component, computed, DestroyRef, inject, OnInit } from '@angular/core';
import { ChargingService, ChargingListDTO, PageDTOChargingListDTO } from '@suac/api';
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
import { DatePipe, DecimalPipe, NgIf } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ResponsiveService } from '../../../common/responsive.service';

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
  ],
})
export class DashboardChargingListComponent implements OnInit {
  private readonly columnsAll = ['time', 'stationId', 'kwh', 'price'];
  private readonly columnsSmall = ['time', 'kwh', 'price'];

  private readonly responsiveService = inject(ResponsiveService);

  public displayedColumns =
    computed(() => this.responsiveService.isMobile() ? this.columnsSmall : this.columnsAll);
  public dataSource = new MatTableDataSource<ChargingListDTO>([]);

  public sort$ = new BehaviorSubject<{
    sortActive: string;
    sortDirection: "asc" | "desc" | "";
  }>({
    sortActive: "time",
    sortDirection: "desc",
  });

  constructor(
    private chargingService: ChargingService,
    private destroyRef: DestroyRef,
  ) {
  }

  ngOnInit(): void {
    this.sort$.pipe(
      switchMap(sort => this.chargingService.getChargingList(0, 5, `${sort.sortActive},${sort.sortDirection}`)),
      map((page: PageDTOChargingListDTO) => page.content || []),
      tap((list: ChargingListDTO[]) => this.dataSource.data = list),
      takeUntilDestroyed(this.destroyRef),
    ).subscribe();
  }

  onSortChange(sort: Sort) {
    this.sort$.next({ sortActive: sort.active, sortDirection: sort.direction });
  }

}
