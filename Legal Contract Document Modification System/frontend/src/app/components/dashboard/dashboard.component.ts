import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { DashboardStats, ModificationRequest, AuditLog } from '../../models/models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  stats: DashboardStats | null = null;
  pendingRequests: ModificationRequest[] = [];
  recentAuditLogs: AuditLog[] = [];
  loading = true;

  constructor(
    public authService: AuthService,
    private apiService: ApiService
  ) {}

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.loading = true;
    this.apiService.getDashboardStats().subscribe({
      next: (data) => {
        this.stats = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });

    if (this.authService.isApprover()) {
      this.apiService.getPendingModifications().subscribe({
        next: (mods) => {
          this.pendingRequests = mods.slice(0, 5);
        }
      });
    }

    if (this.authService.isAdmin() || this.authService.isManager()) {
      this.apiService.getAuditLogs().subscribe({
        next: (logs) => {
          this.recentAuditLogs = logs.slice(0, 6);
        }
      });
    }
  }

  formatRole(role?: string): string {
    if (!role) return '';
    return role.replace('ROLE_', '').replace('_', ' ');
  }
}
