import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { BuildInfoService } from '@suac/api';
import { tap } from 'rxjs';
import { MatToolbar } from "@angular/material/toolbar";
import { MatIcon } from "@angular/material/icon";
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrl: 'footer.component.scss',
  imports: [
    MatToolbar,
    MatIcon,
    DatePipe,
  ],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FooterComponent implements OnInit {
  public version = signal('');
  public versionTime = signal<Date>(new Date(0));
  public year = signal(new Date().getFullYear());

  constructor(
    private buildInfoService: BuildInfoService,
  ) {
  }

  ngOnInit(): void {
    this.buildInfoService.get().pipe(
      tap(version => {
        this.version.set(version.version!);
        this.versionTime.set(version.time!);
      }),
    ).subscribe();
  }

}
