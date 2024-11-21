import { Component, computed, DestroyRef, input, OnInit, output, signal } from '@angular/core';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { BehaviorSubject, combineLatest, debounceTime, Observable, switchMap, tap } from 'rxjs';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { Sort } from '@angular/material/sort';

export interface Page<T> {
  content: Array<T>;
  number: number;
  numberOfElements: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

@Component({
  selector: 'app-paginated-table',
  templateUrl: './paginated-table.component.html',
  imports: [
    MatPaginator,
  ],
  standalone: true,
})
export class PaginatedTableComponent implements OnInit {

  public readonly fetchFn = input.required<(page: number, size: number, sort: string) => Observable<Page<any>>>()
  public readonly dataSource = output<any[]>();

  private readonly defaultSort: Sort = { // TODO make configurable
    active: "time",
    direction: "desc",
  };

  protected readonly pageSizeOptions = [5, 10, 25, 100];
  private readonly sort = signal(this.defaultSort);
  public readonly sortActive = computed(() => this.sort().active);
  public readonly sortDirection = computed(() => this.sort().direction);

  protected readonly total = signal(0);
  protected readonly pageIndex = signal(0);
  protected readonly pageSize = signal(this.pageSizeOptions[0]); // TODO make configurable
  private forceRefresh$ = new BehaviorSubject(true);

  private readonly sortString = computed(() => `${this.sort().active},${this.sort().direction}`);
  private readonly onPageChange$ = combineLatest([
    toObservable(this.pageIndex),
    toObservable(this.pageSize),
    toObservable(this.sortString),
    this.forceRefresh$,
  ]);

  constructor(private readonly destroyRef: DestroyRef) {
  }

  public ngOnInit(): void {
    this.onPageChange$.pipe(
      debounceTime(1), // changing sort and switching to first page would still emit value twice
      switchMap(([page, size, sortString]) => this.fetchFn()(page, size, sortString)),
      tap(page => {
        this.total.set(page.totalElements);
        this.dataSource.emit(page.content);
      }),
      takeUntilDestroyed(this.destroyRef),
    ).subscribe();
  }

  protected pageChanged(pageEvent: PageEvent) {
    this.pageIndex.set(pageEvent.pageIndex);
    this.pageSize.set(pageEvent.pageSize);
  }

  sortChanged(sort: Sort) {
    this.sort.set(sort);
    this.pageIndex.set(0);
  }

  forceRefresh() {
    this.forceRefresh$.next(true);
  }

}
