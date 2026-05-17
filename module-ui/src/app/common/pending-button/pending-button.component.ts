import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-pending-button',
  templateUrl: './pending-button.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [MatButtonModule, MatIconModule, MatProgressSpinnerModule],
})
export class PendingButtonComponent {
  readonly onClick = output<MouseEvent>();
  readonly label = input.required<string>();
  readonly icon = input<string | undefined>(undefined);
  readonly isLoading = input.required<boolean>();
  readonly type = input<'' | 'filled' | 'outlined' | 'elevated' | 'tonal'>('');
}
