import { ChangeDetectionStrategy, Component, computed, OnInit, signal } from '@angular/core';
import { catchError, finalize, take, tap } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { DashboardService, ChargerStatusDTO, PublicInfoService } from '@suac/api';
import { AuthenticationService } from '../../security/authentication.service';

@Component({
  selector: 'app-charging-status',
  templateUrl: './charging-status.component.html',
  styleUrl: './charging-status.component.scss',
  imports: [
    MatButtonModule,
    MatButtonToggleModule,
    MatIconModule,
    MatProgressSpinnerModule,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChargingStatusComponent implements OnInit {
  CarStateEnum = ChargerStatusDTO.CarStateEnum;
  ConnectorStatusOcppEnum = ChargerStatusDTO.ConnectorStatusOcppEnum;

  protected readonly refreshLoading = signal(false);
  protected readonly chargeStatusChangeLoading = signal(false);
  protected readonly currentChangeLoading = signal(false);

  protected readonly statusModel = signal<ChargerStatusDTO>({
    carState: this.CarStateEnum.UnknownOrError,
    connectorStatusOcpp: this.ConnectorStatusOcppEnum.Unavailable,
    occupiedFrom: undefined,
    chargedKwh: 0,
  });
  protected readonly isLoggedIn = computed(() => this.authService.currentUserValue() !== null);
  protected readonly carState = computed(() => this.statusModel().carState!!);
  protected readonly showStart = computed(() =>
    this.carState() !== this.CarStateEnum.Charging && this.carState() !== this.CarStateEnum.Complete);
  protected readonly canBeStarted = computed(() =>
    this.carState() === this.CarStateEnum.WaitCar);
  protected readonly showStop = computed(() =>
    this.carState() === this.CarStateEnum.Charging && this.isLoggedIn());

  constructor(
    private readonly infoService: PublicInfoService,
    private readonly dashboardService: DashboardService,
    private readonly authService: AuthenticationService,
  ) {
  }

  ngOnInit(): void {
    this.refreshStatus();
  }

  public refreshStatus() {
    if (this.refreshLoading()) {
      return;
    }

    this.refreshLoading.set(true);
    this.infoService.getChargerStatus().pipe(
      take(1),
      tap(status => this.statusModel.set(status)),
      finalize(() => this.refreshLoading.set(false)),
    ).subscribe();
  }

  public startCharging(): void {
    if (this.chargeStatusChangeLoading()) {
      return;
    }

    this.chargeStatusChangeLoading.set(true);
    this.dashboardService.startCharging().pipe(
      take(1),
      tap(status => this.statusModel.set(status)),
      finalize(() => this.chargeStatusChangeLoading.set(false)),
    ).subscribe();
  }

  public setCurrent(amp: number): void {
    if (this.currentChangeLoading()) return;

    const previousCurrent = this.statusModel().current;
    this.currentChangeLoading.set(true);
    this.statusModel.update(s => ({ ...s, current: amp }));

    this.dashboardService.setChargingParams(amp).pipe(
      take(1),
      tap(newCurrent => this.statusModel.update(s => ({ ...s, current: newCurrent }))),
      catchError(e => {
        this.statusModel.update(s => ({ ...s, current: previousCurrent }));
        throw e;
      }),
      finalize(() => this.currentChangeLoading.set(false)),
    ).subscribe();
  }

  public stopCharging(): void {
    if (this.chargeStatusChangeLoading()) {
      return;
    }

    this.chargeStatusChangeLoading.set(true);
    this.dashboardService.stopCharging().pipe(
      take(1),
      tap(status => this.statusModel.set(status)),
      finalize(() => this.chargeStatusChangeLoading.set(false)),
    ).subscribe();
  }

}
