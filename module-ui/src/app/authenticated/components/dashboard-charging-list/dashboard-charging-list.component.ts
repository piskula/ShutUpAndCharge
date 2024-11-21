import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ChargingService, ChargingListDTO } from '@suac/api';
import { map, Observable } from 'rxjs';
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
import { DatePipe, DecimalPipe, NgIf } from '@angular/common';
import { ResponsiveService } from '../../../common/responsive.service';
import { Page, PaginatedTableComponent } from '../../../common/paginated-table/paginated-table.component';

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
    PaginatedTableComponent,
  ],
})
export class DashboardChargingListComponent {
  private readonly columnsAll = ['time', 'stationId', 'kwh', 'price'];
  private readonly columnsSmall = ['time', 'kwh', 'price'];

  private readonly responsiveService = inject(ResponsiveService);

  public displayedColumns =
    computed(() => this.responsiveService.isMobile() ? this.columnsSmall : this.columnsAll);
  public dataSource = new MatTableDataSource<ChargingListDTO>([]);

  constructor(
    private chargingService: ChargingService,
  ) {
  }

  public fetchFn = (page: number, size: number, sort: string): Observable<Page<ChargingListDTO>> => {
    return this.chargingService.getChargingList(page, size, sort)
      .pipe(map(page => page as Page<ChargingListDTO>));
  }

}
