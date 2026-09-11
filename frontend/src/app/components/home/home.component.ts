import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

interface WorkflowStep {
  id: number;
  title: string;
  badge: string;
  description: string;
  highlight: string;
  mockContent: {
    contractNo: string;
    action: string;
    original?: string;
    proposed?: string;
    details: string;
  };
}

interface RoleFeature {
  roleKey: string;
  roleName: string;
  badge: string;
  avatarBg: string;
  tagline: string;
  responsibilities: string[];
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  // Workflow Interactive State
  activeStep = 1;
  workflowSteps: WorkflowStep[] = [
    {
      id: 1,
      title: 'Contract Ingestion & Storage',
      badge: 'Step 1: Upload',
      description: 'Create contracts with full metadata and upload legal documents (PDF, DOCX) to the secure storage repository.',
      highlight: 'Centralized document repository with instant streaming download.',
      mockContent: {
        contractNo: 'CNT-2026-001',
        action: 'Document Ingestion',
        details: 'Master SLA Agreement uploaded with 4 core legal attachments and encryption verified.'
      }
    },
    {
      id: 2,
      title: 'Clause Architecture & Sequencing',
      badge: 'Step 2: Organize',
      description: 'Decompose complex legal agreements into structured clauses (Payment Terms, Confidentiality, IP Rights) with dynamic sequence ordering.',
      highlight: 'Dynamic ▲/▼ sequence ordering maintains strict legal clause hierarchy.',
      mockContent: {
        contractNo: 'CNT-2026-001',
        action: 'Clause Reordering',
        details: 'Clause 1: Payment Terms | Clause 2: Confidentiality & NDA | Clause 3: IP Assignment'
      }
    },
    {
      id: 3,
      title: 'Modification Proposal',
      badge: 'Step 3: Propose',
      description: 'Authorized users propose clause-specific or contract-wide modifications while automatically capturing current baseline values.',
      highlight: 'Captures original text, proposed revision, and mandatory business justification.',
      mockContent: {
        contractNo: 'CNT-2026-001',
        action: 'Proposed Modification #1',
        original: 'Payment must be completed within 30 days of verified invoice receipt.',
        proposed: 'Payment must be completed within 45 days of verified invoice receipt.',
        details: 'Reason: Extended billing cycle requested by enterprise accounts payable.'
      }
    },
    {
      id: 4,
      title: 'Visual Diff & Approval Workflow',
      badge: 'Step 4: Review & Diff',
      description: 'Legal approvers review pending requests using a side-by-side visual diff viewer. Approvals require comments; rejections require formal reasons.',
      highlight: 'Side-by-side color-coded diff ensures zero ambiguity before sign-off.',
      mockContent: {
        contractNo: 'CNT-2026-001',
        action: 'Approver Decision',
        original: '30 days net payment',
        proposed: '45 days net payment',
        details: 'Status: APPROVED by David Vance (Legal Approver). Comments: Verified compliance.'
      }
    },
    {
      id: 5,
      title: 'Version Snapshot & Audit Trail',
      badge: 'Step 5: Version & Audit',
      description: 'Approvals automatically trigger a new version snapshot (e.g. v1.0 → v1.1) archiving all clauses, while the immutable audit trail logs every system action.',
      highlight: 'Complete traceability: actor, timestamp, action, and historic snapshot recall.',
      mockContent: {
        contractNo: 'CNT-2026-001',
        action: 'Version Snapshot v1.1',
        details: 'Archived snapshot created. System Audit Log #9: MODIFICATION_APPROVED recorded.'
      }
    }
  ];

  // Roles Interactive State
  activeRoleIndex = 2; // Default to Approver
  rolesList: RoleFeature[] = [
    {
      roleKey: 'admin',
      roleName: 'System Administrator',
      badge: 'ROLE_ADMIN',
      avatarBg: '#fee2e2',
      tagline: 'Complete enterprise oversight, RBAC governance, and audit verification.',
      responsibilities: [
        'User management: provision, edit, and deactivate users with role-based policies',
        'Inspect full system-wide audit logs and tamper-evident event trails',
        'Global contract lifecycle governance, system metrics, and administrative overrides'
      ]
    },
    {
      roleKey: 'manager',
      roleName: 'Contract Manager',
      badge: 'ROLE_CONTRACT_MANAGER',
      avatarBg: '#f0fdf4',
      tagline: 'Author and orchestrate legal contracts, clauses, and document repositories.',
      responsibilities: [
        'Draft and register legal contracts with status lifecycle state machine tracking',
        'Upload, preview, and manage attached digital agreement files',
        'Structure, edit, and dynamically sequence atomic contract clauses',
        'Track pending modification requests and review version timeline'
      ]
    },
    {
      roleKey: 'approver',
      roleName: 'Legal Approver',
      badge: 'ROLE_APPROVER',
      avatarBg: '#fffbeb',
      tagline: 'Reviews proposed revisions with side-by-side visual redline diffs.',
      responsibilities: [
        'Dedicated Approver Workspace with real-time pending queue indicators',
        'Interactive side-by-side diff comparison between baseline & proposed text',
        'Ratify amendments with mandatory legal ratification comments',
        'Reject non-compliant amendments with recorded statutory justifications'
      ]
    },
    {
      roleKey: 'user',
      roleName: 'Client / Internal Representative',
      badge: 'ROLE_USER',
      avatarBg: '#eff6ff',
      tagline: 'Inspects active contracts and submits structured modification requests.',
      responsibilities: [
        'Search and inspect active legal agreements, versions, and clauses',
        'Propose structured amendments to specific clauses with business justifications',
        'Monitor approval status lifecycle (Pending, Approved, Rejected) in real time'
      ]
    }
  ];

  constructor(public authService: AuthService) {}

  selectStep(stepId: number): void {
    this.activeStep = stepId;
  }

  selectRole(index: number): void {
    this.activeRoleIndex = index;
  }
}
