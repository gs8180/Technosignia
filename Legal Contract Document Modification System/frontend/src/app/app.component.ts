import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, Router, NavigationEnd, RouterModule } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { AuthService } from './services/auth.service';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterModule, NavbarComponent, SidebarComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  title = 'Legal Contract Document Modification System';
  currentUrl = '';

  constructor(
    public authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.currentUrl = this.router.url;
    this.router.events.pipe(
      filter((event): event is NavigationEnd => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      this.currentUrl = event.urlAfterRedirects || event.url;
    });
  }

  isDashboardView(): boolean {
    if (!this.authService.isLoggedIn()) return false;
    const path = this.currentUrl.split('?')[0];
    return path !== '/' && path !== '/home' && path !== '/login' && path !== '';
  }

  getPageTitle(): string {
    const path = this.currentUrl.split('?')[0];
    if (path.startsWith('/contracts/')) return 'Contract Details';
    if (path.startsWith('/contracts')) return 'Contracts Catalog';
    if (path.startsWith('/approvals')) return 'Legal Approver Queue';
    if (path.startsWith('/audit-logs')) return 'Immutable Audit Trail';
    if (path.startsWith('/users')) return 'User Management & RBAC';
    if (path.startsWith('/dashboard')) return 'Executive Dashboard';
    return 'Workspace';
  }

  formatRole(role?: string): string {
    if (!role) return '';
    switch (role) {
      case 'ROLE_ADMIN': return 'Administrator';
      case 'ROLE_CONTRACT_MANAGER': return 'Contract Manager';
      case 'ROLE_APPROVER': return 'Legal Approver';
      case 'ROLE_USER': return 'Client User';
      default: return role.replace('ROLE_', '');
    }
  }
}
