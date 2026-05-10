import { HttpInterceptorFn } from '@angular/common/http';
import { from } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { keycloakService } from '../security/keycloak.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  return from(keycloakService.updateToken(30)).pipe(
    switchMap(() => {
      const token = keycloakService.getToken();

      if (token) {
        req = req.clone({
          setHeaders: {
            Authorization: `Bearer ${token}`,
          },
        });
      }

      return next(req);
    })
  );
};
