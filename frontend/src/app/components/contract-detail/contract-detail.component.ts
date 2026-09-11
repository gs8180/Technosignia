import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import {
  Contract,
  Clause,
  DocumentAttachment,
  ModificationRequest,
  ContractVersion,
  ModificationType
} from '../../models/models';

@Component({
  selector: 'app-contract-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './contract-detail.component.html',
  styleUrls: ['./contract-detail.component.css']
})
export class ContractDetailComponent implements OnInit {
  contractId!: number;
  contract: Contract | null = null;
  clauses: Clause[] = [];
  documents: DocumentAttachment[] = [];
  modifications: ModificationRequest[] = [];
  versions: ContractVersion[] = [];

  activeTab: 'clauses' | 'documents' | 'modifications' | 'versions' = 'clauses';
  loading = true;

  // Clause Modal State
  showClauseModal = false;
  isEditClause = false;
  editClauseId: number | null = null;
  clauseFormTitle = '';
  clauseFormContent = '';
  clauseFormOrder: number | null = null;
  clauseModalError = '';

  // Document Upload State
  selectedFile: File | null = null;
  uploading = false;
  uploadError = '';

  // Modification Request Modal State (Task 9)
  showModModal = false;
  modTargetType: ModificationType = 'CLAUSE';
  modSelectedClauseId: number | null = null;
  modOriginalValue = '';
  modProposedValue = '';
  modReason = '';
  modModalError = '';

  // Version Snapshot Modal State (Task 11)
  selectedSnapshotVersion: ContractVersion | null = null;
  snapshotClauses: any[] = [];
  showSnapshotModal = false;

  constructor(
    private route: ActivatedRoute,
    private apiService: ApiService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.contractId = +idParam;
      this.loadContractData();
    }
  }

  loadContractData(): void {
    this.loading = true;
    this.apiService.getContractById(this.contractId).subscribe({
      next: (data) => {
        this.contract = data;
        this.loadClauses();
        this.loadDocuments();
        this.loadModifications();
        this.loadVersions();
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  loadClauses(): void {
    this.apiService.getClauses(this.contractId).subscribe({
      next: (data) => (this.clauses = data)
    });
  }

  loadDocuments(): void {
    this.apiService.getDocuments(this.contractId).subscribe({
      next: (data) => (this.documents = data)
    });
  }

  loadModifications(): void {
    this.apiService.getModificationsForContract(this.contractId).subscribe({
      next: (data) => (this.modifications = data)
    });
  }

  loadVersions(): void {
    this.apiService.getContractVersions(this.contractId).subscribe({
      next: (data) => (this.versions = data)
    });
  }

  // ================= CLAUSE MANAGEMENT =================
  openAddClauseModal(): void {
    this.isEditClause = false;
    this.editClauseId = null;
    this.clauseFormTitle = '';
    this.clauseFormContent = '';
    this.clauseFormOrder = this.clauses.length + 1;
    this.clauseModalError = '';
    this.showClauseModal = true;
  }

  openEditClauseModal(clause: Clause): void {
    this.isEditClause = true;
    this.editClauseId = clause.id;
    this.clauseFormTitle = clause.title;
    this.clauseFormContent = clause.content;
    this.clauseFormOrder = clause.clauseOrder;
    this.clauseModalError = '';
    this.showClauseModal = true;
  }

  saveClause(): void {
    if (!this.clauseFormTitle || !this.clauseFormContent) {
      this.clauseModalError = 'Please provide title and content';
      return;
    }

    const payload = {
      title: this.clauseFormTitle,
      content: this.clauseFormContent,
      clauseOrder: this.clauseFormOrder || this.clauses.length + 1
    };

    if (this.isEditClause && this.editClauseId) {
      this.apiService.updateClause(this.contractId, this.editClauseId, payload).subscribe({
        next: () => {
          this.showClauseModal = false;
          this.loadClauses();
        },
        error: (err) => {
          this.clauseModalError = err.error?.message || 'Error updating clause';
        }
      });
    } else {
      this.apiService.addClause(this.contractId, payload).subscribe({
        next: () => {
          this.showClauseModal = false;
          this.loadClauses();
        },
        error: (err) => {
          this.clauseModalError = err.error?.message || 'Error adding clause';
        }
      });
    }
  }

  deleteClause(clauseId: number): void {
    if (confirm('Delete this clause from the contract?')) {
      this.apiService.deleteClause(this.contractId, clauseId).subscribe({
        next: () => this.loadClauses(),
        error: (err) => alert(err.error?.message || 'Error deleting clause')
      });
    }
  }

  moveClause(index: number, direction: 'up' | 'down'): void {
    if (direction === 'up' && index === 0) return;
    if (direction === 'down' && index === this.clauses.length - 1) return;

    const newClauses = [...this.clauses];
    const targetIndex = direction === 'up' ? index - 1 : index + 1;
    const temp = newClauses[index];
    newClauses[index] = newClauses[targetIndex];
    newClauses[targetIndex] = temp;

    const orderedIds = newClauses.map((c) => c.id);
    this.apiService.reorderClauses(this.contractId, orderedIds).subscribe({
      next: (updated) => (this.clauses = updated)
    });
  }

  // ================= DOCUMENT MANAGEMENT =================
  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  uploadFile(): void {
    if (!this.selectedFile) return;

    this.uploading = true;
    this.uploadError = '';

    this.apiService.uploadDocument(this.contractId, this.selectedFile).subscribe({
      next: () => {
        this.uploading = false;
        this.selectedFile = null;
        this.loadDocuments();
      },
      error: (err) => {
        this.uploading = false;
        this.uploadError = err.error?.message || 'Error uploading document';
      }
    });
  }

  downloadDoc(doc: DocumentAttachment): void {
    window.open(this.apiService.downloadDocumentUrl(this.contractId, doc.id), '_blank');
  }

  deleteDoc(documentId: number): void {
    if (confirm('Delete this document attachment?')) {
      this.apiService.deleteDocument(this.contractId, documentId).subscribe({
        next: () => this.loadDocuments(),
        error: (err) => alert(err.error?.message || 'Error deleting document')
      });
    }
  }

  // ================= MODIFICATION REQUEST =================
  openModificationModal(targetClause?: Clause): void {
    if (targetClause) {
      this.modTargetType = 'CLAUSE';
      this.modSelectedClauseId = targetClause.id;
      this.modOriginalValue = targetClause.content;
    } else {
      this.modTargetType = 'CONTRACT';
      this.modSelectedClauseId = null;
      this.modOriginalValue = this.contract?.description || '';
    }

    this.modProposedValue = '';
    this.modReason = '';
    this.modModalError = '';
    this.showModModal = true;
  }

  onModTargetChange(): void {
    if (this.modTargetType === 'CONTRACT') {
      this.modSelectedClauseId = null;
      this.modOriginalValue = this.contract?.description || '';
    } else {
      if (this.clauses.length > 0) {
        this.modSelectedClauseId = this.clauses[0].id;
        this.modOriginalValue = this.clauses[0].content;
      }
    }
  }

  onModClauseSelectChange(): void {
    const clause = this.clauses.find((c) => c.id === +this.modSelectedClauseId!);
    if (clause) {
      this.modOriginalValue = clause.content;
    }
  }

  submitModification(): void {
    if (!this.modProposedValue || !this.modReason) {
      this.modModalError = 'Please provide both proposed modification and reason';
      return;
    }

    const payload = {
      contractId: this.contractId,
      clauseId: this.modTargetType === 'CLAUSE' ? +this.modSelectedClauseId! : undefined,
      requestType: this.modTargetType,
      proposedValue: this.modProposedValue,
      reason: this.modReason
    };

    this.apiService.createModification(payload).subscribe({
      next: () => {
        this.showModModal = false;
        this.loadModifications();
        this.activeTab = 'modifications';
        alert('Modification request submitted successfully with status PENDING!');
      },
      error: (err) => {
        this.modModalError = err.error?.message || 'Error submitting modification request';
      }
    });
  }

  // ================= VERSION SNAPSHOT =================
  viewSnapshot(v: ContractVersion): void {
    this.selectedSnapshotVersion = v;
    try {
      this.snapshotClauses = JSON.parse(v.clausesSnapshotJson || '[]');
    } catch {
      this.snapshotClauses = [];
    }
    this.showSnapshotModal = true;
  }
}
