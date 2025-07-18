import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-price-colored',
  templateUrl: './price-colored.component.html',
  styleUrl: './price-colored.component.scss',
  imports: [
    CurrencyPipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PriceColoredComponent {
  public readonly value = input.required<number>()
  public readonly bold = input(false)
}
