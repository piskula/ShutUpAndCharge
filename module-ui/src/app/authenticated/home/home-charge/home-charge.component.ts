import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, signal } from '@angular/core';
import { filter, map, tap } from 'rxjs';
import { HeaderData } from '../../../common/header/model/header-data';
import { ChargingStatusComponent } from '../../../common/charging-status/charging-status.component';
import { DashboardChargingListComponent } from '../dashboard-charging-list/dashboard-charging-list.component';
import { AuthenticationService } from '../../../security/authentication.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-home-charge',
  templateUrl: './home-charge.component.html',
  styleUrl: 'home-charge.component.scss',
  imports: [
    ChargingStatusComponent,
    DashboardChargingListComponent,
  ],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomeChargeComponent implements OnInit {
  public headerData = signal<HeaderData>({ user: '', roles: []});

  constructor(
    private authenticationService: AuthenticationService,
    private destroyRef: DestroyRef,
  ) {
  }

  ngOnInit(): void {
    this.authenticationService.currentUser().pipe(
      map(user => {
        if (user == null)
          return null;
        else
          return ({
            user: user.name,
            roles: user.roles,
          } as HeaderData);
      }),
      tap((headerData: HeaderData | null) => {
        if (headerData != null) {
          this.headerData.set(headerData);
        }
      }),
      takeUntilDestroyed(this.destroyRef),
    ).subscribe();
  }

}
