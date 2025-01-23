import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { NgIf } from '@angular/common';
import { ChargingService, ChargerStatusDTO } from '@suac/api';
import { finalize, take, tap } from 'rxjs';

@Component({
  selector: 'app-charging-status',
  templateUrl: './charging-status.component.html',
  styleUrl: './charging-status.component.scss',
  imports: [
    MatButton,
    MatIcon,
    NgIf,
    MatIconButton,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChargingStatusComponent implements OnInit {

  protected readonly isBeingRefreshed = signal(false);
  protected readonly status = signal<ChargerStatusDTO>({
    awaitingAuthorization: false,
    chargedKwh: 0,
    occupied: false,
    occupiedFrom: undefined,
    offline: true,
  });

  constructor(
    private readonly chargingService: ChargingService,
  ) {
  }

  ngOnInit(): void {
    this.refreshStatus();
  }

  public refreshStatus() {
    if (this.isBeingRefreshed()) {
      return;
    }

    this.isBeingRefreshed.set(true);
    this.chargingService.getChargerStatus().pipe(
      take(1),
      tap(status => this.status.set(status)),
      finalize(() => this.isBeingRefreshed.set(false)),
    ).subscribe();
  }
}
