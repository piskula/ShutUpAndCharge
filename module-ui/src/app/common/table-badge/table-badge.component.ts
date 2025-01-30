import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { NgIf } from '@angular/common';
import { MatChip, MatChipAvatar, MatChipSet } from '@angular/material/chips';

@Component({
  selector: 'app-table-badge',
  templateUrl: './table-badge.component.html',
  styleUrl: './table-badge.component.scss',
  imports: [
    MatIcon,
    NgIf,
    MatChip,
    MatChipSet,
    MatChipAvatar,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TableBadgeComponent {

  public readonly icon = input<string | undefined>(undefined);
  public readonly varColor = input<string>('inherit');
  public readonly varBackground = input<string>('inherit');

}
