import { ChangeDetectionStrategy, Component, computed, inject, input, output } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ResponsiveService } from '../responsive.service';

@Component({
  selector: 'app-pending-button',
  templateUrl: './pending-button.component.html',
  styleUrl: 'pending-button.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [MatButtonModule, MatIconModule, MatProgressSpinnerModule],
})
export class PendingButtonComponent {
  private readonly responsiveService = inject(ResponsiveService);
  protected readonly isMobile = computed(() => this.responsiveService.isMobile());

  readonly onClick = output<MouseEvent>();
  readonly label = input.required<string>();
  readonly icon = input<string | undefined>(undefined);
  readonly isLoading = input.required<boolean>();
  readonly type = input<'' | 'filled' | 'outlined' | 'elevated' | 'tonal'>('');
}
