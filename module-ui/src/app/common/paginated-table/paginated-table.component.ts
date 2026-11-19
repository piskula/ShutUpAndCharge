import {
  ChangeDetectionStrategy,
  Component,
  computed,
  DestroyRef,
  input,
  OnChanges,
  OnInit,
  output,
  signal,
  SimpleChanges,
} from '@angular/core';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { BehaviorSubject, catchError, combineLatest, debounceTime, finalize, Observable, of, switchMap, tap } from 'rxjs';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { Sort } from '@angular/material/sort';
import { PendingButtonComponent } from '../pending-button/pending-button.component';
import { HttpResponse } from '@angular/common/http';
import { downloadFile } from '../download.util';

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
  styleUrl: 'paginated-table.component.scss',
  imports: [
    MatPaginatorModule,
    MatProgressBarModule,
    PendingButtonComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PaginatedTableComponent implements OnInit, OnChanges {

  public readonly fetchFn = input.required<(page: number, size: number, sort: string) => Observable<Page<any>>>();
  public readonly dataSource = output<any[]>();


  public readonly defaultSort = input<Sort>({
    active: 'id',
    direction: 'desc',
  });

  protected readonly pageSizeOptions = [5, 10, 25, 100];
  public readonly defaultPageSize = input<number>(this.pageSizeOptions[1]);
  public readonly exportFn = input<((sort: string) => Observable<HttpResponse<Blob>>) | undefined>(undefined);

  private readonly sort = signal(this.defaultSort());
  public readonly sortActive = computed(() => this.sort().active);
  public readonly sortDirection = computed(() => this.sort().direction);

  protected readonly total = signal(0);
  protected readonly pageIndex = signal(0);
  protected readonly pageSize = signal(this.defaultPageSize());
  protected readonly isLoading = signal(false);
  protected readonly isExporting = signal(false);
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

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['defaultSort']) {
      this.sort.set(changes['defaultSort'].currentValue);
    }
    if (changes['defaultPageSize']) {
      this.pageSize.set(changes['defaultPageSize'].currentValue);
    }
  }

  public ngOnInit(): void {
    this.onPageChange$.pipe(
      debounceTime(1), // changing sort and switching to first page would still emit value twice
      tap(() => this.isLoading.set(true)),
      switchMap(([page, size, sortString]) => this.fetchFn()(page, size, sortString).pipe(
        // Handle errors per request, so a failed fetch doesn't terminate the stream and later refreshes still work.
        // The user is already notified by the HTTP error interceptor.
        catchError(() => of(this.emptyPage(size))),
      )),
      tap(page => {
        this.total.set(page.totalElements);
        this.dataSource.emit(page.content);
        this.isLoading.set(false);
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

  /** Re-fetches the current page, keeping page index, page size and sort. */
  forceRefresh() {
    this.forceRefresh$.next(true);
  }

  /** Goes back to the first page and re-fetches (e.g. after filters changed). Results in exactly one fetch. */
  resetAndRefresh() {
    if (this.pageIndex() !== 0) {
      // Changing the page index already triggers a fetch via onPageChange$ (also resets the bound paginator).
      this.pageIndex.set(0);
    } else {
      this.forceRefresh$.next(true);
    }
  }

  private emptyPage(size: number): Page<any> {
    return { content: [], number: 0, numberOfElements: 0, size, totalElements: 0, totalPages: 0 };
  }

  protected onExport(): void {
    const fn = this.exportFn();
    if (!fn) return;

    this.isExporting.set(true);
    fn(this.sortString()).pipe(
      tap(file => downloadFile(file)),
      finalize(() => this.isExporting.set(false)),
      takeUntilDestroyed(this.destroyRef),
    ).subscribe();
  }
}
