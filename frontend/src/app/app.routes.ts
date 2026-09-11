import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { ContractListComponent } from './components/contract-list/contract-list.component';
import { ContractDetailComponent } from './components/contract-detail/contract-detail.component';
import { ApproverDashboardComponent } from './components/approver-dashboard/approver-dashboard.component';
import { AuditLogComponent } from './components/audit-log/audit-log.component';
import { UserManagementComponent } from './components/user-management/user-management.component';
import { authGuard } from './services/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'home', component: HomeComponent, title: 'ContractFlow - Legal Contract Document Modification System' },
  { path: 'login', component: LoginComponent, title: 'Sign In - Legal Contract System' },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [authGuard],
    title: 'Dashboard - Legal Contract System'
  },
  {
    path: 'contracts',
    component: ContractListComponent,
    canActivate: [authGuard],
    title: 'Contracts - Legal Contract System'
  },
  {
    path: 'contracts/:id',
    component: ContractDetailComponent,
    canActivate: [authGuard],
    title: 'Contract Details - Legal Contract System'
  },
  {
    path: 'approvals',
    component: ApproverDashboardComponent,
    canActivate: [authGuard],
    data: { roles: ['ROLE_APPROVER', 'ROLE_ADMIN'] },
    title: 'Approver Dashboard - Legal Contract System'
  },
  {
    path: 'audit-logs',
    component: AuditLogComponent,
    canActivate: [authGuard],
    data: { roles: ['ROLE_ADMIN', 'ROLE_CONTRACT_MANAGER'] },
    title: 'Audit Log - Legal Contract System'
  },
  {
    path: 'users',
    component: UserManagementComponent,
    canActivate: [authGuard],
    data: { roles: ['ROLE_ADMIN'] },
    title: 'User Management - Legal Contract System'
  },
  { path: '**', redirectTo: '' }
];
