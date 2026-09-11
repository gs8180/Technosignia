import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  Contract,
  ContractStatus,
  Clause,
  DocumentAttachment,
  ModificationRequest,
  ModificationType,
  ContractVersion,
  AuditLog,
  DashboardStats,
  User,
  Role
} from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private readonly BASE_URL = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  // =================== CONTRACTS ===================
  getContracts(search?: string, status?: ContractStatus): Observable<Contract[]> {
    let params = new HttpParams();
    if (search && search.trim() !== '') {
      params = params.set('search', search.trim());
    }
    if (status) {
      params = params.set('status', status);
    }
    return this.http.get<Contract[]>(`${this.BASE_URL}/contracts`, { params });
  }

  getContractById(id: number): Observable<Contract> {
    return this.http.get<Contract>(`${this.BASE_URL}/contracts/${id}`);
  }

  createContract(contract: { contractNumber: string; title: string; description: string; status: ContractStatus }): Observable<Contract> {
    return this.http.post<Contract>(`${this.BASE_URL}/contracts`, contract);
  }

  updateContract(id: number, contract: { contractNumber: string; title: string; description: string; status: ContractStatus }): Observable<Contract> {
    return this.http.put<Contract>(`${this.BASE_URL}/contracts/${id}`, contract);
  }

  deleteContract(id: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.BASE_URL}/contracts/${id}`);
  }

  getDashboardStats(): Observable<DashboardStats> {
    return this.http.get<DashboardStats>(`${this.BASE_URL}/contracts/dashboard-stats`);
  }

  // =================== CLAUSES ===================
  getClauses(contractId: number): Observable<Clause[]> {
    return this.http.get<Clause[]>(`${this.BASE_URL}/contracts/${contractId}/clauses`);
  }

  addClause(contractId: number, clause: { title: string; content: string; clauseOrder?: number }): Observable<Clause> {
    return this.http.post<Clause>(`${this.BASE_URL}/contracts/${contractId}/clauses`, clause);
  }

  updateClause(contractId: number, clauseId: number, clause: { title: string; content: string; clauseOrder?: number }): Observable<Clause> {
    return this.http.put<Clause>(`${this.BASE_URL}/contracts/${contractId}/clauses/${clauseId}`, clause);
  }

  deleteClause(contractId: number, clauseId: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.BASE_URL}/contracts/${contractId}/clauses/${clauseId}`);
  }

  reorderClauses(contractId: number, clauseIds: number[]): Observable<Clause[]> {
    return this.http.put<Clause[]>(`${this.BASE_URL}/contracts/${contractId}/clauses/reorder`, { clauseIds });
  }

  // =================== DOCUMENTS ===================
  getDocuments(contractId: number): Observable<DocumentAttachment[]> {
    return this.http.get<DocumentAttachment[]>(`${this.BASE_URL}/contracts/${contractId}/documents`);
  }

  uploadDocument(contractId: number, file: File): Observable<DocumentAttachment> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<DocumentAttachment>(`${this.BASE_URL}/contracts/${contractId}/documents`, formData);
  }

  downloadDocumentUrl(contractId: number, documentId: number): string {
    return `${this.BASE_URL}/contracts/${contractId}/documents/${documentId}/download`;
  }

  deleteDocument(contractId: number, documentId: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.BASE_URL}/contracts/${contractId}/documents/${documentId}`);
  }

  // =================== MODIFICATIONS ===================
  getAllModifications(): Observable<ModificationRequest[]> {
    return this.http.get<ModificationRequest[]>(`${this.BASE_URL}/modifications`);
  }

  getPendingModifications(): Observable<ModificationRequest[]> {
    return this.http.get<ModificationRequest[]>(`${this.BASE_URL}/modifications/pending`);
  }

  getMyModifications(): Observable<ModificationRequest[]> {
    return this.http.get<ModificationRequest[]>(`${this.BASE_URL}/modifications/my-requests`);
  }

  getModificationsForContract(contractId: number): Observable<ModificationRequest[]> {
    return this.http.get<ModificationRequest[]>(`${this.BASE_URL}/modifications/contract/${contractId}`);
  }

  getModificationById(id: number): Observable<ModificationRequest> {
    return this.http.get<ModificationRequest>(`${this.BASE_URL}/modifications/${id}`);
  }

  createModification(data: {
    contractId: number;
    clauseId?: number;
    requestType: ModificationType;
    proposedValue: string;
    reason: string;
  }): Observable<ModificationRequest> {
    return this.http.post<ModificationRequest>(`${this.BASE_URL}/modifications`, data);
  }

  approveModification(id: number, comment?: string): Observable<ModificationRequest> {
    return this.http.post<ModificationRequest>(`${this.BASE_URL}/modifications/${id}/approve`, { comment: comment || 'Approved' });
  }

  rejectModification(id: number, rejectionReason: string): Observable<ModificationRequest> {
    return this.http.post<ModificationRequest>(`${this.BASE_URL}/modifications/${id}/reject`, { rejectionReason });
  }

  // =================== VERSIONS ===================
  getContractVersions(contractId: number): Observable<ContractVersion[]> {
    return this.http.get<ContractVersion[]>(`${this.BASE_URL}/contracts/${contractId}/versions`);
  }

  // =================== AUDIT LOGS ===================
  getAuditLogs(entityName?: string, entityId?: number): Observable<AuditLog[]> {
    let params = new HttpParams();
    if (entityName && entityId) {
      params = params.set('entityName', entityName).set('entityId', entityId.toString());
    }
    return this.http.get<AuditLog[]>(`${this.BASE_URL}/audit-logs`, { params });
  }

  // =================== USERS (ADMIN) ===================
  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.BASE_URL}/users`);
  }

  createUser(user: { username: string; email: string; password: string; fullName: string; role: Role }): Observable<User> {
    return this.http.post<User>(`${this.BASE_URL}/users`, user);
  }

  updateUser(id: number, data: { fullName?: string; email?: string; role?: Role; active?: boolean; password?: string }): Observable<User> {
    return this.http.put<User>(`${this.BASE_URL}/users/${id}`, data);
  }

  deleteUser(id: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.BASE_URL}/users/${id}`);
  }
}
