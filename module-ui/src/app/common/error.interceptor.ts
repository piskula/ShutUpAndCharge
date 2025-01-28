import { HttpErrorResponse, HttpEvent, HttpHandlerFn, HttpRequest } from '@angular/common/http';
import { catchError, Observable } from 'rxjs';
import { inject } from '@angular/core';
import { SnackbarService } from './snackbar.service';

export function errorInterceptor(req: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> {
  const snackbarService = inject(SnackbarService);

  return next(req).pipe(
    catchError((error: unknown) => {
      if (error instanceof HttpErrorResponse) {
        showErrorIfPossible(error, snackbarService);
      }
      throw error;
    }),
  );
}

export function showErrorIfPossible(err: HttpErrorResponse, snackbarService: SnackbarService): void {
  if (typeof err.error === 'object' && 'userMessage' in err.error) {
    snackbarService.showErrorSnackBar(err.error.userMessage);
  } else if (err.status === 500) {
    snackbarService.showErrorSnackBar('Internal server error. Contact us, please.');
  }
}
