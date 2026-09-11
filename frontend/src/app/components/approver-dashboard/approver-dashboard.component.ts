import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { ModificationRequest } from '../../models/models';

@Component({
  selector: 'app-approver-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './approver-dashboard.component.html',
  styleUrls: ['./approver-dashboard.component.css']
})
export class ApproverDashboardComponent implements OnInit {
  pendingRequests: ModificationRequest[] = [];
  allRequests: ModificationRequest[] = [];
  viewFilter: 'PENDING' | 'ALL' = 'PENDING';
  loading = true;

  // Selected Request for Review
  selectedRequest: ModificationRequest | null = null;
  showReviewModal = false;

  // Approve Dialog State
  showApproveModal = false;
  approveComment = '';

  // Reject Dialog State
  showRejectModal = false;
  rejectionReason = '';
  rejectError = '';

  constructor(
    private apiService: ApiService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadRequests();
  }

  loadRequests(): void {
    this.loading = true;
    this.apiService.getPendingModifications().subscribe({
      next: (data) => {
        this.pendingRequests = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });

    this.apiService.getAllModifications().subscribe({
      next: (data) => {
        this.allRequests = data;
      }
    });
  }

  openReview(req: ModificationRequest): void {
    this.selectedRequest = req;
    this.showReviewModal = true;
  }

  closeReview(): void {
    this.showReviewModal = false;
  }

  openApproveDialog(): void {
    this.approveComment = 'Approved and verified by legal counsel.';
    this.showApproveModal = true;
  }

  confirmApprove(): void {
    if (!this.selectedRequest) return;

    this.apiService.approveModification(this.selectedRequest.id, this.approveComment).subscribe({
      next: (updated) => {
        alert(`Modification #${updated.id} successfully APPROVED! A new contract version snapshot has been generated.`);
        this.showApproveModal = false;
        this.showReviewModal = false;
        this.loadRequests();
      },
      error: (err) => {
        alert(err.error?.message || 'Error approving request');
      }
    });
  }

  openRejectDialog(): void {
    this.rejectionReason = '';
    this.rejectError = '';
    this.showRejectModal = true;
  }

  confirmReject(): void {
    if (!this.selectedRequest) return;
    if (!this.rejectionReason || this.rejectionReason.trim() === '') {
      this.rejectError = 'Rejection reason is strictly required';
      return;
    }

    this.apiService.rejectModification(this.selectedRequest.id, this.rejectionReason).subscribe({
      next: (updated) => {
        alert(`Modification #${updated.id} was REJECTED with recorded reason.`);
        this.showRejectModal = false;
        this.showReviewModal = false;
        this.loadRequests();
      },
      error: (err) => {
        this.rejectError = err.error?.message || 'Error rejecting request';
      }
    });
  }
}
