import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MatIconRegistry } from '@angular/material/icon';

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
  ) {
  }

  ngOnInit(): void {
    this.matIconRegistry.setDefaultFontSetClass('material-symbols-outlined');
  }

}
