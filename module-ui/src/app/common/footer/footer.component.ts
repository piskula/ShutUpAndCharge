import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { tap } from 'rxjs';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { PublicInfoService } from '@suac/api';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrl: 'footer.component.scss',
  imports: [
    MatToolbarModule,
    MatIconModule,
    DatePipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FooterComponent implements OnInit {
  public version = signal('');
  public versionTime = signal<Date>(new Date(0));
  public year = signal(new Date().getFullYear());

  constructor(
    private infoService: PublicInfoService,
  ) {
  }

  ngOnInit(): void {
    this.infoService.getVersion().pipe(
      tap(version => {
        this.version.set(version.version!);
        this.versionTime.set(version.time!);
      }),
    ).subscribe();
  }

}
