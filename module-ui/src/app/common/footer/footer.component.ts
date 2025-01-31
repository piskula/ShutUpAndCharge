import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { MatIcon } from '@angular/material/icon';
import { MatToolbar } from '@angular/material/toolbar';
import { PublicInfoService } from '@suac/api';
import { tap } from 'rxjs';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrl: 'footer.component.scss',
  imports: [
    MatToolbar,
    MatIcon,
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
