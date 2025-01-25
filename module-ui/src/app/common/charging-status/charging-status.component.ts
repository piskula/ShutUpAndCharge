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
  CarStateEnum = ChargerStatusDTO.CarStateEnum;
  ConnectorStatusOcppEnum = ChargerStatusDTO.ConnectorStatusOcppEnum;

  protected readonly isBeingRefreshed = signal(false);
  protected readonly status = signal<ChargerStatusDTO>({
    carState: this.CarStateEnum.UnknownOrError,
    connectorStatusOcpp: this.ConnectorStatusOcppEnum.Unavailable,
    occupiedFrom: undefined,
    chargedKwh: 0,
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
