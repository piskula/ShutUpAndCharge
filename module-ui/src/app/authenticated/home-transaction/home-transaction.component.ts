import { ChangeDetectionStrategy, Component, signal } from '@angular/core';
import { TransactionListComponent } from '../components/transaction-list/transaction-list.component';
import {
  TransactionTemporaryListComponent
} from '../components/transaction-temporary-list/transaction-temporary-list.component';
import { AuthenticationService } from '../../security/authentication.service';
import { TemporaryTransactionService } from '@suac/api';
import { finalize, take } from 'rxjs';
import { PendingButtonComponent } from '../../common/pending-button/pending-button.component';

@Component({
  selector: 'app-home-transaction',
  templateUrl: './home-transaction.component.html',
  styleUrl: 'home-transaction.component.scss',
  imports: [
    TransactionListComponent,
    TransactionTemporaryListComponent,
    PendingButtonComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomeTransactionComponent {

  protected isOngoing = signal(false);

  constructor(
    protected readonly authService: AuthenticationService,
    private readonly temporaryTransactionService: TemporaryTransactionService,
  ) {
  }

  enforceSync() {
    this.isOngoing.set(true);
    this.temporaryTransactionService.enforceDownloadAndSync().pipe(
      take(1),
      finalize(() => this.isOngoing.set(false)),
    ).subscribe();
  }

}
