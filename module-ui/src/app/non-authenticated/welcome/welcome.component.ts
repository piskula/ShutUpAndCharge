import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatToolbar } from "@angular/material/toolbar";
import { MatAnchor, MatButton, MatIconButton } from "@angular/material/button";
import { MatIcon } from "@angular/material/icon";
import { MatBadge } from "@angular/material/badge";
import { FooterComponent } from "../../common/footer/footer.component";
import { ChargingStatusComponent } from '../../common/charging-status/charging-status.component';

@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  styleUrl: './welcome.component.scss',
  imports: [
    RouterLink,
    MatToolbar,
    MatButton,
    MatIcon,
    MatIconButton,
    MatAnchor,
    FooterComponent,
    ChargingStatusComponent,
  ],
  standalone: true,
})
export class WelcomeComponent {
  isFree = true;
  year = (new Date()).getFullYear();
}
