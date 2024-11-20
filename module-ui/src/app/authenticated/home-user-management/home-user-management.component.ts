import { ChangeDetectionStrategy, Component } from '@angular/core';
import { UserManagementListComponent } from '../components/user-management-list/user-management-list.component';

@Component({
  selector: 'app-home-user-management',
  templateUrl: './home-user-management.component.html',
  styleUrl: 'home-user-management.component.scss',
  imports: [
    UserManagementListComponent,
  ],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomeUserManagementComponent {

  constructor() {
  }

}
