import {
  ChangeDetectionStrategy,
  Component,
  computed,
  DestroyRef,
  inject,
  input,
  LOCALE_ID,
  signal,
} from '@angular/core';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { FinishedTransactionService, MonthlyKwhChartDTO, TransactionFilterDTO } from '@suac/api';
import { NgxEchartsDirective, provideEchartsCore } from 'ngx-echarts';
// tree-shaken ECharts build: only what is needed for the stacked area chart
import * as echarts from 'echarts/core';
import type { EChartsCoreOption } from 'echarts/core';
import { LineChart } from 'echarts/charts';
import { GridComponent, LegendComponent, TooltipComponent } from 'echarts/components';
import { CanvasRenderer } from 'echarts/renderers';
import { catchError, of, switchMap, tap } from 'rxjs';

echarts.use([LineChart, GridComponent, TooltipComponent, LegendComponent, CanvasRenderer]);

/** Distinct colors readable on both light and dark backgrounds, used for the top users. */
const PALETTE = ['#4285f4', '#ea8a00', '#0f9d58', '#c2185b', '#8e5bd6'];
const COLOR_OTHER = '#9e9e9e';

interface TooltipParam {
  marker?: string;
  seriesName?: string;
  value?: unknown;
  axisValueLabel?: string;
}

interface ChartTheme {
  text: string;
  textMuted: string;
  line: string;
  tooltipBackground: string;
}

@Component({
  selector: 'app-transaction-kwh-chart',
  templateUrl: './transaction-kwh-chart.component.html',
  styleUrl: './transaction-kwh-chart.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [MatProgressBarModule, NgxEchartsDirective],
  providers: [provideEchartsCore({ echarts })],
})
export class TransactionKwhChartComponent {
  readonly filter = input.required<TransactionFilterDTO>();

  readonly #transactionService = inject(FinishedTransactionService);
  readonly #locale = inject(LOCALE_ID);
  readonly #destroyRef = inject(DestroyRef);

  /** Incremented to force a reload with an unchanged filter. */
  private readonly refreshTick = signal(0);
  private readonly prefersDark = signal(false);

  protected readonly isLoading = signal(true);
  protected readonly hasError = signal(false);
  private readonly data = signal<MonthlyKwhChartDTO | null>(null);

  protected readonly isEmpty = computed(() => (this.data()?.series?.length ?? 0) === 0);

  protected readonly options = computed<EChartsCoreOption>(() => {
    const data = this.data();
    // dependency on prefersDark makes the options (and thus theme colors) rebuild when the color scheme changes
    this.prefersDark();
    if (!data || (data.series?.length ?? 0) === 0) {
      return {};
    }
    return this.buildOptions(data, this.readTheme());
  });

  constructor() {
    toObservable(computed(() => ({ filter: this.filter(), tick: this.refreshTick() }))).pipe(
      tap(() => {
        this.isLoading.set(true);
        this.hasError.set(false);
      }),
      // switchMap cancels the in-flight request when the filter changes
      switchMap(({ filter }) => this.#transactionService.getMonthlyKwh(filter).pipe(
        // Handle errors per request so later reloads still work. The user is already notified by the HTTP error interceptor.
        catchError(() => {
          this.hasError.set(true);
          return of(null);
        }),
      )),
      tap(data => {
        this.data.set(data);
        this.isLoading.set(false);
      }),
      takeUntilDestroyed(this.#destroyRef),
    ).subscribe();

    this.watchColorScheme();
  }

  refresh(): void {
    this.refreshTick.update(v => v + 1);
  }

  private watchColorScheme(): void {
    if (typeof window === 'undefined' || typeof window.matchMedia !== 'function') return;
    const query = window.matchMedia('(prefers-color-scheme: dark)');
    this.prefersDark.set(query.matches);
    const listener = (event: MediaQueryListEvent) => this.prefersDark.set(event.matches);
    query.addEventListener('change', listener);
    this.#destroyRef.onDestroy(() => query.removeEventListener('change', listener));
  }

  private readTheme(): ChartTheme {
    const style = getComputedStyle(document.documentElement);
    const read = (name: string, fallback: string) => style.getPropertyValue(name).trim() || fallback;
    const dark = this.prefersDark();
    return {
      text: read('--mat-sys-on-surface', dark ? '#e3e3e3' : '#1b1b1f'),
      textMuted: read('--mat-sys-on-surface-variant', dark ? '#c4c6d0' : '#44474f'),
      line: read('--mat-sys-outline-variant', dark ? '#44474f' : '#c4c6d0'),
      tooltipBackground: read('--mat-sys-surface-container', dark ? '#1e1f23' : '#f0f0f5'),
    };
  }

  private buildOptions(data: MonthlyKwhChartDTO, theme: ChartTheme): EChartsCoreOption {
    const monthFormat = new Intl.DateTimeFormat(this.#locale, { month: 'short', year: 'numeric' });
    const numberFormat = new Intl.NumberFormat(this.#locale, { maximumFractionDigits: 0 });
    const formatKwh = (value: number) => `${numberFormat.format(value)} kWh`;
    const formatMonth = (month: string) => {
      const [year, monthIndex] = month.split('-').map(Number);
      return monthFormat.format(new Date(year, monthIndex - 1, 1));
    };

    let paletteIndex = 0;
    const series = (data.series ?? []).map(s => {
      const color = s.other ? COLOR_OTHER : PALETTE[paletteIndex++ % PALETTE.length];
      return {
        name: s.name ?? '',
        type: 'line',
        stack: 'total',
        areaStyle: {},
        smooth: false,
        symbol: 'circle',
        emphasis: { focus: 'series' },
        itemStyle: { color },
        lineStyle: { color },
        data: s.kwh ?? [],
      };
    });

    return {
      backgroundColor: 'transparent',
      textStyle: { color: theme.text },
      grid: { top: 72, left: 8, right: 16, bottom: 8, containLabel: true },
      legend: {
        type: 'scroll',
        top: 0,
        textStyle: { color: theme.text },
        pageTextStyle: { color: theme.text },
        pageIconColor: theme.text,
        pageIconInactiveColor: theme.textMuted,
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'cross', label: { show: false } },
        confine: true,
        backgroundColor: theme.tooltipBackground,
        borderColor: theme.line,
        textStyle: { color: theme.text },
        formatter: (raw: unknown) => {
          const params = (Array.isArray(raw) ? raw : [raw]) as TooltipParam[];
          const valueOf = (p: TooltipParam) => (typeof p.value === 'number' ? p.value : 0);
          const rows = params
            .filter(p => valueOf(p) !== 0)
            .map(p => `<div>${p.marker ?? ''} ${escapeHtml(p.seriesName ?? '')}: <b>${formatKwh(valueOf(p))}</b></div>`)
            .join('');
          const total = params.reduce((sum, p) => sum + valueOf(p), 0);
          return `<div>${escapeHtml(params[0]?.axisValueLabel ?? '')}</div>${rows}`
            + `<div style="margin-top: 4px"><b>Total: ${formatKwh(total)}</b></div>`;
        },
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: (data.months ?? []).map(formatMonth),
        axisLine: { lineStyle: { color: theme.line } },
        axisLabel: { color: theme.textMuted },
      },
      yAxis: {
        type: 'value',
        name: 'kWh',
        nameTextStyle: { color: theme.textMuted },
        axisLabel: { color: theme.textMuted },
        splitLine: { lineStyle: { color: theme.line } },
      },
      series,
    };
  }
}

function escapeHtml(value: string): string {
  return value
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}
