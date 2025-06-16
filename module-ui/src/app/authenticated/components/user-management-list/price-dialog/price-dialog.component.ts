import { ChangeDetectionStrategy, Component, inject, model } from '@angular/core';
import { MatButton } from '@angular/material/button';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle,
} from '@angular/material/dialog';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { CurrencyPipe } from '@angular/common';

export interface PriceDialogData {
  accountName: string;
  price: number;
}

@Component({
  selector: 'app-price-dialog',
  templateUrl: './price-dialog.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    FormsModule,
    MatButton,
    MatDialogActions,
    MatDialogClose,
    MatDialogContent,
    MatDialogTitle,
    MatFormField,
    MatInput,
    MatLabel,
    CurrencyPipe,
  ],
})
export class PriceDialogComponent {

  suggestedPrices = [5, 10, 20, 30, 50];

  readonly data = inject<PriceDialogData>(MAT_DIALOG_DATA);
  protected amount = model(this.data.price);

  constructor(private readonly dialogRef: MatDialogRef<PriceDialogComponent>) {
  }

  close(): void {
    this.dialogRef.close();
  }

}
