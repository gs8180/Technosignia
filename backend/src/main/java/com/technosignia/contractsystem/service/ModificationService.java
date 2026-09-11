package com.technosignia.contractsystem.service;

import com.technosignia.contractsystem.dto.ApprovalRequest;
import com.technosignia.contractsystem.dto.ModificationCreateRequest;
import com.technosignia.contractsystem.dto.RejectionRequest;
import com.technosignia.contractsystem.entity.*;
import com.technosignia.contractsystem.repository.ClauseRepository;
import com.technosignia.contractsystem.repository.ContractRepository;
import com.technosignia.contractsystem.repository.ModificationRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ModificationService {

    @Autowired
    private ModificationRequestRepository modificationRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ClauseRepository clauseRepository;

    @Autowired
    private VersionService versionService;

    @Autowired
    private AuthService authService;

    @Autowired
    private AuditService auditService;

    public List<ModificationRequest> getAllModifications() {
        return modificationRepository.findAll();
    }

    public List<ModificationRequest> getPendingModifications() {
        return modificationRepository.findByStatusOrderByRequestedAtDesc(ModificationStatus.PENDING);
    }

    public List<ModificationRequest> getModificationsForContract(Long contractId) {
        return modificationRepository.findByContractIdOrderByRequestedAtDesc(contractId);
    }

    public List<ModificationRequest> getModificationsByCurrentUser() {
        User currentUser = authService.getCurrentUser();
        return modificationRepository.findByRequestedByIdOrderByRequestedAtDesc(currentUser.getId());
    }

    public ModificationRequest getModificationById(Long id) {
        return modificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Modification request not found with id: " + id));
    }

    @Transactional
    public ModificationRequest createModificationRequest(ModificationCreateRequest request) {
        Contract contract = contractRepository.findById(request.getContractId())
                .orElseThrow(() -> new IllegalArgumentException("Contract not found with id: " + request.getContractId()));

        User currentUser = authService.getCurrentUser();
        ModificationRequest mod = new ModificationRequest();
        mod.setContract(contract);
        mod.setRequestType(request.getRequestType());
        mod.setProposedValue(request.getProposedValue());
        mod.setReason(request.getReason());
        mod.setRequestedBy(currentUser);
        mod.setStatus(ModificationStatus.PENDING);

        if (request.getRequestType() == ModificationType.CLAUSE) {
            if (request.getClauseId() == null) {
                throw new IllegalArgumentException("Clause ID must be provided for clause modifications");
            }
            Clause clause = clauseRepository.findById(request.getClauseId())
                    .orElseThrow(() -> new IllegalArgumentException("Clause not found with id: " + request.getClauseId()));
            mod.setClause(clause);
            mod.setOriginalValue(clause.getContent());
        } else {
            // Whole contract description modification
            mod.setOriginalValue(contract.getDescription() != null ? contract.getDescription() : "");
        }

        ModificationRequest saved = modificationRepository.save(mod);

        auditService.log("MODIFICATION_REQUESTED", currentUser.getUsername(), "Contract", contract.getId(),
                "Submitted " + mod.getRequestType() + " modification request #" + saved.getId() + " for: " + contract.getTitle());

        return saved;
    }

    @Transactional
    public ModificationRequest approveModification(Long id, ApprovalRequest approvalRequest) {
        ModificationRequest mod = getModificationById(id);

        if (mod.getStatus() != ModificationStatus.PENDING) {
            throw new IllegalStateException("Only PENDING modification requests can be approved");
        }

        User approver = authService.getCurrentUser();
        mod.setStatus(ModificationStatus.APPROVED);
        mod.setReviewedBy(approver);
        mod.setReviewedAt(LocalDateTime.now());
        mod.setApproverComment(approvalRequest != null ? approvalRequest.getComment() : "Approved");

        Contract contract = mod.getContract();

        // Apply changes to Contract or Clause
        String changeSummary = "";
        if (mod.getRequestType() == ModificationType.CLAUSE && mod.getClause() != null) {
            Clause clause = mod.getClause();
            clause.setContent(mod.getProposedValue());
            clauseRepository.save(clause);
            changeSummary = "Clause '" + clause.getTitle() + "' updated via Modification #" + mod.getId();
        } else {
            contract.setDescription(mod.getProposedValue());
            contractRepository.save(contract);
            changeSummary = "Contract terms updated via Modification #" + mod.getId();
        }

        // Create new contract version snapshot automatically upon approval (Task 11)
        versionService.createVersionSnapshot(contract, approver, changeSummary);

        ModificationRequest saved = modificationRepository.save(mod);

        auditService.log("MODIFICATION_APPROVED", approver.getUsername(), "Contract", contract.getId(),
                "Approved modification #" + mod.getId() + " (" + mod.getRequestType() + "). Comments: " + mod.getApproverComment());

        return saved;
    }

    @Transactional
    public ModificationRequest rejectModification(Long id, RejectionRequest rejectionRequest) {
        ModificationRequest mod = getModificationById(id);

        if (mod.getStatus() != ModificationStatus.PENDING) {
            throw new IllegalStateException("Only PENDING modification requests can be rejected");
        }

        if (rejectionRequest == null || rejectionRequest.getRejectionReason() == null || rejectionRequest.getRejectionReason().isBlank()) {
            throw new IllegalArgumentException("A rejection reason must be provided");
        }

        User approver = authService.getCurrentUser();
        mod.setStatus(ModificationStatus.REJECTED);
        mod.setReviewedBy(approver);
        mod.setReviewedAt(LocalDateTime.now());
        mod.setRejectionReason(rejectionRequest.getRejectionReason());

        ModificationRequest saved = modificationRepository.save(mod);

        auditService.log("MODIFICATION_REJECTED", approver.getUsername(), "Contract", mod.getContract().getId(),
                "Rejected modification #" + mod.getId() + ". Reason: " + mod.getRejectionReason());

        return saved;
    }
}
