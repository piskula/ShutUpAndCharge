import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, signal } from '@angular/core';
import { map, tap } from 'rxjs';
import { HeaderData } from '../../common/header/model/header-data';
import { ChargingStatusComponent } from '../../common/charging-status/charging-status.component';
import { DashboardChargingListComponent } from '../components/dashboard-charging-list/dashboard-charging-list.component';
import { AuthenticationService } from '../../security/authentication.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-home-dashboard',
  templateUrl: './home-dashboard.component.html',
  styleUrl: 'home-dashboard.component.scss',
  imports: [
    ChargingStatusComponent,
    DashboardChargingListComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomeDashboardComponent implements OnInit {
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
