import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-home-transaction',
  templateUrl: './home-transaction.component.html',
  styleUrl: 'home-transaction.component.scss',
  imports: [
  ],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomeTransactionComponent {

}
