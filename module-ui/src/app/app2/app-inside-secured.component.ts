import { Component, OnInit } from '@angular/core';
import { CurrentUserService } from '@trucker/api';
import { combineLatest, tap } from 'rxjs';

@Component({
  selector: 'app-inside-secured',
  standalone: true,
  templateUrl: './app-inside-secured.component.html',
})
export class InsideSecuredComponent implements OnInit {
  user = '';

  constructor(
    private currentUserService: CurrentUserService,
  ) {
  }

  ngOnInit(): void {
    combineLatest([
      this.currentUserService.getCurrentUser(),
    ]).pipe(
      tap(([user]) => this.user = user.email || 'email????'),
    ).subscribe();
  }

  getSth(): void {
    this.currentUserService.getSecured().subscribe();
  }

  postSth(): void {
    this.currentUserService.postSecured('<<<I am posting this>>>').subscribe();
  }

}
