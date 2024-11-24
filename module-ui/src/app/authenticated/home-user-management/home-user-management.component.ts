import { ChangeDetectionStrategy, Component } from '@angular/core';
import { UserManagementListComponent } from '../components/user-management-list/user-management-list.component';
import {PaginatedTableComponent} from '../../common/paginated-table/paginated-table.component';

@Component({
  selector: 'app-home-user-management',
  templateUrl: './home-user-management.component.html',
  styleUrl: 'home-user-management.component.scss',
  imports: [
    UserManagementListComponent,
    PaginatedTableComponent,
  ],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomeUserManagementComponent {

  constructor() {
  }

}
