import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { HomeDashboardComponent } from './home-dashboard/home-dashboard.component';
import { HomeUserManagementComponent } from './home-user-management/home-user-management.component';
import { HomeTransactionComponent } from './home-transaction/home-transaction.component';

const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'dashboard',
      },
      {
        path: 'dashboard',
        component: HomeDashboardComponent,
      },
      {
        path: 'transaction',
        component: HomeTransactionComponent,
      },
      {
        path: 'userManagement',
        component: HomeUserManagementComponent,
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AuthenticatedRoutingModule { }
