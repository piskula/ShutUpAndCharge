import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root',
})
export class SnackbarService {

  constructor(private readonly snackBar: MatSnackBar) {
  }

  public showErrorSnackBar(msg: string): void {
    this.snackBar.open(msg, 'Close', {
      duration: 150000,
      panelClass: ['snackbar-error'],
    });
  }

  public showInfoSnackBar(msg: string): void {
    this.snackBar.open(msg, undefined, {
      duration: 5000,
      panelClass: ['snackbar-success'],
    });
  }

}
