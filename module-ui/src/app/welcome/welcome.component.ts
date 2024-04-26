import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
// import { environment } from '../../environments/environment';

@Component({
  selector: 'app-welcome',
  standalone: true,
  templateUrl: './welcome.component.html',
  imports: [
    RouterLink
  ],
  // styleUrl: './welcome.component.css'
})
export class WelcomeComponent {
  // isProduction = environment.production;

}
