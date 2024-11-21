import {
  Component,
  computed,
  DestroyRef,
  EventEmitter,
  input,
  Input,
  OnInit,
  output,
  Output,
  signal,
} from '@angular/core';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { combineLatest, debounceTime, map, tap } from 'rxjs';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { Sort } from '@angular/material/sort';

export interface Pageable {
  page: number,
  size: number,
  sort: string,
}

@Component({
  selector: 'app-paginated-table',
  templateUrl: './paginated-table.component.html',
  styleUrl: './paginated-table.component.scss',
  imports: [
    MatPaginator,
  ],
  standalone: true,
})
export class PaginatedTableComponent implements OnInit {

  public readonly total = input.required<number>();
  public readonly onPageChange = output<Pageable>();

  protected readonly pageSizeOptions = [5, 10, 25, 100];
  private readonly defaultSort: {
    sortActive: string;
    sortDirection: "asc" | "desc" | "";
  } = {
    // TODO make configurable
    sortActive: "time",
    sortDirection: "desc",
  };
  protected readonly pageIndex = signal(0);
  protected readonly pageSize = signal(this.pageSizeOptions[0]);
  public readonly sort = signal(this.defaultSort);
  private readonly sortString = computed(() => `${this.sort().sortActive},${this.sort().sortDirection}`);
  private readonly onPageChange$ = combineLatest([
    toObservable(this.pageIndex),
    toObservable(this.pageSize),
    toObservable(this.sortString),
  ]);

  constructor(private readonly destroyRef: DestroyRef) {
  }

  ngOnInit(): void {
    this.onPageChange$.pipe(
      debounceTime(1), // changing sort and switching to first page would still emit value twice
      map(([page, size, sortString]) => ({
        page: page,
        size: size,
        sort: sortString,
      } as Pageable)),
      tap(page => this.onPageChange.emit(page)),
      takeUntilDestroyed(this.destroyRef),
    ).subscribe();
  }

  pageChanged(pageEvent: PageEvent) {
    this.pageIndex.set(pageEvent.pageIndex);
    this.pageSize.set(pageEvent.pageSize);
  }

  sortChanged(sort: Sort) {
    this.sort.set({ sortActive: sort.active, sortDirection: sort.direction });
    this.pageIndex.set(0);
  }

}
