import { Component, DestroyRef, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MatIconRegistry } from '@angular/material/icon';
import { SwUpdate } from '@angular/service-worker';
import { SnackbarService } from './common/snackbar.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent implements OnInit {
  title = 'TruckerFM';

  constructor(
    private readonly matIconRegistry: MatIconRegistry,
    private readonly updates: SwUpdate,
    private readonly snackBarService: SnackbarService,
    private readonly destroyRef: DestroyRef,
  ) {
    this.performCheckForPwaUpdate()
  }

  ngOnInit(): void {
    this.matIconRegistry.setDefaultFontSetClass('material-symbols-outlined');
  }

  private performCheckForPwaUpdate(): void {
    this.updates.versionUpdates.pipe(
      takeUntilDestroyed(this.destroyRef),
    ).subscribe((evt) => {
      switch (evt.type) {
        // case 'VERSION_DETECTED':
          // console.log(`Downloading new app version: ${evt.version.hash}`);
          // break;
        case 'VERSION_READY':
          this.snackBarService.showInfoSnackBar('New app version ready for use!');
          // console.log(`Current app version: ${evt.currentVersion.hash}`);
          // console.log(`New app version ready for use: ${evt.latestVersion.hash}`);
          break;
      }
    });
  }

}
