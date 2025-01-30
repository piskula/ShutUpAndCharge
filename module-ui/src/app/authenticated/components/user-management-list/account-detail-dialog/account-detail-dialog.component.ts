import { ChangeDetectionStrategy, Component, inject, model } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { AccountDTO } from '@suac/api';
import { MatButtonModule } from '@angular/material/button';
import { JsonPipe } from '@angular/common';

@Component({
  selector: 'app-account-detail-dialog',
  templateUrl: './account-detail-dialog.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    MatDialogModule,
    MatButtonModule,
    JsonPipe,
  ],
})
export class AccountDetailDialogComponent {

  readonly data = inject<AccountDTO>(MAT_DIALOG_DATA);
  protected chipUid = model(this.data.assignedChipUid ?? '');

  constructor(private readonly dialogRef: MatDialogRef<AccountDetailDialogComponent>) {
  }

}
