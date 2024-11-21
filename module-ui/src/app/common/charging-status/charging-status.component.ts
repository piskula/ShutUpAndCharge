import { ChangeDetectionStrategy, Component, signal } from '@angular/core';
import { MatButton, MatMiniFabButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-charging-status',
  templateUrl: './charging-status.component.html',
  styleUrl: './charging-status.component.scss',
  imports: [
    MatButton,
    MatIcon,
    NgIf,
    MatMiniFabButton,
  ],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChargingStatusComponent {
  public isFree = signal(Math.random() < 0.5);

  public refreshStatus() {
    this.isFree.update(prev => !prev);
  }
}
