import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-not-found',
  standalone: true,
  templateUrl: './not-found.component.html',
})
export class NotFoundComponent {

  constructor(
    private router: Router,
  ) {
    this.triggerRoutingIfRedirectedAfterLogin();
  }

  private triggerRoutingIfRedirectedAfterLogin() {
    if (this.router.url.startsWith('/api/tryLogin?continue')) {
      this.router.navigate(['/']);
    }
  }

}
