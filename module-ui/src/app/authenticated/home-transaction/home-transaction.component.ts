import { ChangeDetectionStrategy, Component } from '@angular/core';
import { TransactionListComponent } from '../components/transaction-list/transaction-list.component';
import {
  TransactionTemporaryListComponent
} from '../components/transaction-temporary-list/transaction-temporary-list.component';
import { AuthenticationService } from '../../security/authentication.service';

@Component({
  selector: 'app-home-transaction',
  templateUrl: './home-transaction.component.html',
  styleUrl: 'home-transaction.component.scss',
  imports: [
    TransactionListComponent,
    TransactionTemporaryListComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomeTransactionComponent {

  constructor(protected readonly authService: AuthenticationService) {
  }

}
