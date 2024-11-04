import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
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
import { DatePipe, DecimalPipe } from '@angular/common';

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
  ],
})
export class DashboardChargingListComponent implements OnInit {

  public displayedColumns = ['time', 'stationId', 'kwh', 'price'];
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
  ) {
  }

  ngOnInit(): void {
    this.sort$.pipe(
      switchMap(sort => this.chargingService.getChargingList(0, 5, `${sort.sortActive},${sort.sortDirection}`)),
      map((page: PageDTOChargingListDTO) => page.content || []),
      tap((list: ChargingListDTO[]) => this.dataSource.data = list),
    ).subscribe();
  }

  onSortChange(sort: Sort) {
    this.sort$.next({ sortActive: sort.active, sortDirection: sort.direction });
  }

}
