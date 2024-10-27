import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { CurrentUserService } from '@suac/api';
import { combineLatest, tap } from 'rxjs';
import { MatIcon } from "@angular/material/icon";
import { MatAnchor, MatButton, MatIconButton } from "@angular/material/button";
import { MatToolbar } from "@angular/material/toolbar";
import { FooterComponent } from "../../common/footer/footer.component";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: 'home.component.scss',
  imports: [
    MatIcon,
    MatIconButton,
    MatToolbar,
    MatAnchor,
    MatButton,
    FooterComponent
  ],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomeComponent implements OnInit {
  public user = signal('');

  constructor(
    private currentUserService: CurrentUserService,
  ) {
  }

  ngOnInit(): void {
    combineLatest([
      this.currentUserService.getCurrentUser(),
    ]).pipe(
      tap(([user]) => this.user.set(`${user.firstName} ${user.lastName}`)),
    ).subscribe();
  }

  getSth(): void {
    this.currentUserService.getSecured().subscribe();
  }

  postSth(): void {
    this.currentUserService.postSecured('<<<I am posting this>>>').subscribe();
  }

}
