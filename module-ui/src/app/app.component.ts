import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { BuildInfoService } from '@suac/api';
import { tap } from 'rxjs';
import { MatIconRegistry } from '@angular/material/icon';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent implements OnInit {
  title = 'TruckerFM';
  version = '';
  url = '';

  constructor(
    private buildInfoService: BuildInfoService,
    private readonly matIconRegistry: MatIconRegistry,
  ) {
  }

  ngOnInit(): void {
    this.matIconRegistry.setDefaultFontSetClass('material-symbols-outlined');
    this.buildInfoService.get().pipe(
      tap(version => {
        this.version = version.version || '';
        this.url = version.url || '';
      }),
    ).subscribe();
  }

}
