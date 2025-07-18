import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { finalize, take, tap } from 'rxjs';
import { DashboardService } from '@suac/api';
import { PriceColoredComponent } from '../price-colored/price-colored.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-my-balance',
  templateUrl: './my-balance.component.html',
  styleUrl: './my-balance.component.scss',
  imports: [
    PriceColoredComponent,
    MatProgressSpinnerModule,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MyBalanceComponent implements OnInit {
  protected readonly refreshLoading = signal(false);
  protected readonly balance = signal(0);

  constructor(
    private readonly dashboardService: DashboardService,
  ) {
  }

  ngOnInit(): void {
    this.refreshLoading.set(true);
    this.dashboardService.getMyBalance().pipe(
      take(1),
      tap(balance => this.balance.set(balance)),
      finalize(() => this.refreshLoading.set(false)),
    ).subscribe();
  }

}
