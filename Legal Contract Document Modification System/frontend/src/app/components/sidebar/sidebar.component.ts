import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ApiService } from '../../services/api.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit, OnDestroy {
  collapsed = false;
  pendingApprovalsCount = 0;
  private sub?: Subscription;

  constructor(
    public authService: AuthService,
    private apiService: ApiService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (this.authService.isApprover()) {
      this.loadPendingCount();
    }
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  loadPendingCount(): void {
    this.sub = this.apiService.getPendingModifications().subscribe({
      next: (mods) => {
        this.pendingApprovalsCount = mods.length;
      },
      error: () => {
        this.pendingApprovalsCount = 0;
      }
    });
  }

  toggleCollapse(): void {
    this.collapsed = !this.collapsed;
  }

  logout(): void {
    this.authService.logout();
  }

  formatRole(role?: string): string {
    if (!role) return '';
    switch (role) {
      case 'ROLE_ADMIN': return 'System Admin';
      case 'ROLE_CONTRACT_MANAGER': return 'Contract Manager';
      case 'ROLE_APPROVER': return 'Legal Approver';
      case 'ROLE_USER': return 'Client User';
      default: return role.replace('ROLE_', '');
    }
  }
}
