import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import { keycloakService } from './app/security/keycloak.service';

keycloakService.init().then(() => {
  bootstrapApplication(AppComponent, appConfig)
    .catch((err) => console.error(err));
})
