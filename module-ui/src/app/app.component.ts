import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { BuildInfoService } from '@trucker/api';
import { tap } from 'rxjs';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  title = 'TruckerFM';
  version = '';
  url = '';

  constructor(
    private buildInfoService: BuildInfoService,
  ) {
  }

  ngOnInit(): void {
    this.buildInfoService.get().pipe(
      tap(version => {
        this.version = version.version || '';
        this.url = version.url || '';
      }),
    ).subscribe();
  }

}
