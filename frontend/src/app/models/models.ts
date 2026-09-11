export type Role = 'ROLE_ADMIN' | 'ROLE_CONTRACT_MANAGER' | 'ROLE_APPROVER' | 'ROLE_USER';

export interface User {
  id: number;
  username: string;
  email: string;
  fullName: string;
  role: Role;
  active: boolean;
  createdAt?: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  id: number;
  username: string;
  email: string;
  fullName: string;
  role: Role;
}

export type ContractStatus = 'DRAFT' | 'ACTIVE' | 'EXPIRED' | 'TERMINATED';

export interface Contract {
  id: number;
  contractNumber: string;
  title: string;
  description: string;
  status: ContractStatus;
  currentVersion: string;
  createdBy?: User;
  createdAt?: string;
  updatedAt?: string;
}

export interface Clause {
  id: number;
  title: string;
  content: string;
  clauseOrder: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface DocumentAttachment {
  id: number;
  fileName: string;
  originalFileName: string;
  fileType: string;
  fileSize: number;
  uploadedBy?: User;
  uploadedAt: string;
}

export type ModificationType = 'CONTRACT' | 'CLAUSE';
export type ModificationStatus = 'PENDING' | 'APPROVED' | 'REJECTED';

export interface ModificationRequest {
  id: number;
  contract: Contract;
  clause?: Clause;
  requestType: ModificationType;
  originalValue: string;
  proposedValue: string;
  reason: string;
  status: ModificationStatus;
  requestedBy: User;
  reviewedBy?: User;
  approverComment?: string;
  rejectionReason?: string;
  requestedAt: string;
  reviewedAt?: string;
}

export interface ContractVersion {
  id: number;
  versionNumber: string;
  title: string;
  description: string;
  clausesSnapshotJson: string;
  changedBy?: User;
  changeSummary: string;
  createdAt: string;
}

export interface AuditLog {
  id: number;
  action: string;
  performedBy: string;
  entityName: string;
  entityId: number;
  details: string;
  timestamp: string;
}

export interface DashboardStats {
  totalContracts: number;
  activeContracts: number;
  draftContracts: number;
  expiredContracts: number;
  terminatedContracts: number;
  pendingModifications: number;
  approvedModifications: number;
  rejectedModifications: number;
  totalDocuments: number;
  totalClauses: number;
  totalUsers: number;
}
