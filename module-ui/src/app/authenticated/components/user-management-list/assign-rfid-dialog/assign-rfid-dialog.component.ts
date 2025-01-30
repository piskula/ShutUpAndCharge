import { ChangeDetectionStrategy, Component, inject, model } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';

export interface AssignRfidDialogData {
  accountName: string;
  assignedChipUid?: string;
}

@Component({
  selector: 'app-assign-rfid-dialog',
  templateUrl: './assign-rfid-dialog.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    MatFormFieldModule,
    FormsModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatInputModule,
  ],
})
export class AssignRfidDialogComponent {

  readonly data = inject<AssignRfidDialogData>(MAT_DIALOG_DATA);
  protected chipUid = model(this.data.assignedChipUid ?? '');

  constructor(private readonly dialogRef: MatDialogRef<AssignRfidDialogComponent>) {
  }

}
