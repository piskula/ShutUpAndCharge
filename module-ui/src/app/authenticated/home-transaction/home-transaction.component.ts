import { ChangeDetectionStrategy, Component } from '@angular/core';
import { TransactionListComponent } from '../components/transaction-list/transaction-list.component';

@Component({
  selector: 'app-home-transaction',
  templateUrl: './home-transaction.component.html',
  styleUrl: 'home-transaction.component.scss',
  imports: [
    TransactionListComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomeTransactionComponent {

}
