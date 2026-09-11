import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { Contract, ContractStatus } from '../../models/models';

@Component({
  selector: 'app-contract-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './contract-list.component.html',
  styleUrls: ['./contract-list.component.css']
})
export class ContractListComponent implements OnInit {
  contracts: Contract[] = [];
  loading = false;
  searchQuery = '';
  selectedStatus: ContractStatus | '' = '';

  // Modal State for New / Edit Contract
  showModal = false;
  isEditMode = false;
  editContractId: number | null = null;
  formContractNumber = '';
  formTitle = '';
  formDescription = '';
  formStatus: ContractStatus = 'DRAFT';
  modalError = '';

  constructor(
    private apiService: ApiService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadContracts();
  }

  loadContracts(): void {
    this.loading = true;
    const statusParam = this.selectedStatus ? (this.selectedStatus as ContractStatus) : undefined;
    this.apiService.getContracts(this.searchQuery, statusParam).subscribe({
      next: (data) => {
        this.contracts = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  onFilterChange(): void {
    this.loadContracts();
  }

  openCreateModal(): void {
    this.isEditMode = false;
    this.editContractId = null;
    this.formContractNumber = 'CNT-' + new Date().getFullYear() + '-' + Math.floor(100 + Math.random() * 900);
    this.formTitle = '';
    this.formDescription = '';
    this.formStatus = 'DRAFT';
    this.modalError = '';
    this.showModal = true;
  }

  openEditModal(contract: Contract, event: Event): void {
    event.stopPropagation();
    this.isEditMode = true;
    this.editContractId = contract.id;
    this.formContractNumber = contract.contractNumber;
    this.formTitle = contract.title;
    this.formDescription = contract.description;
    this.formStatus = contract.status;
    this.modalError = '';
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
  }

  saveContract(): void {
    if (!this.formContractNumber || !this.formTitle) {
      this.modalError = 'Please provide contract number and title';
      return;
    }

    const payload = {
      contractNumber: this.formContractNumber,
      title: this.formTitle,
      description: this.formDescription,
      status: this.formStatus
    };

    if (this.isEditMode && this.editContractId) {
      this.apiService.updateContract(this.editContractId, payload).subscribe({
        next: () => {
          this.closeModal();
          this.loadContracts();
        },
        error: (err) => {
          this.modalError = err.error?.message || 'Error updating contract';
        }
      });
    } else {
      this.apiService.createContract(payload).subscribe({
        next: () => {
          this.closeModal();
          this.loadContracts();
        },
        error: (err) => {
          this.modalError = err.error?.message || 'Error creating contract';
        }
      });
    }
  }

  deleteContract(id: number, event: Event): void {
    event.stopPropagation();
    if (confirm('Are you sure you want to delete this contract? All related documents, clauses, and history will be permanently removed.')) {
      this.apiService.deleteContract(id).subscribe({
        next: () => {
          this.loadContracts();
        },
        error: (err) => {
          alert(err.error?.message || 'Error deleting contract');
        }
      });
    }
  }
}
