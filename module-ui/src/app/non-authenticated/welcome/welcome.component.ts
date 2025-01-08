import { Component } from '@angular/core';
import { MatAnchor } from "@angular/material/button";
import { MatIcon } from "@angular/material/icon";
import { FooterComponent } from "../../common/footer/footer.component";
import { ChargingStatusComponent } from '../../common/charging-status/charging-status.component';
import { HeaderComponent } from '../../common/header/header.component';
import { NgOptimizedImage } from '@angular/common';

@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  styleUrl: './welcome.component.scss',
  imports: [
    FooterComponent,
    HeaderComponent,
    ChargingStatusComponent,
    MatIcon,
    MatAnchor,
    NgOptimizedImage,
  ],
})
export class WelcomeComponent {
}
