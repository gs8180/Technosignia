import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { AuditLog } from '../../models/models';

@Component({
  selector: 'app-audit-log',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './audit-log.component.html',
  styleUrls: ['./audit-log.component.css']
})
export class AuditLogComponent implements OnInit {
  logs: AuditLog[] = [];
  filteredLogs: AuditLog[] = [];
  searchTerm = '';
  loading = true;

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.loadLogs();
  }

  loadLogs(): void {
    this.loading = true;
    this.apiService.getAuditLogs().subscribe({
      next: (data) => {
        this.logs = data;
        this.filteredLogs = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  onSearch(): void {
    const term = this.searchTerm.toLowerCase().trim();
    if (!term) {
      this.filteredLogs = this.logs;
    } else {
      this.filteredLogs = this.logs.filter(
        (l) =>
          l.action.toLowerCase().includes(term) ||
          l.performedBy.toLowerCase().includes(term) ||
          (l.details && l.details.toLowerCase().includes(term))
      );
    }
  }
}
