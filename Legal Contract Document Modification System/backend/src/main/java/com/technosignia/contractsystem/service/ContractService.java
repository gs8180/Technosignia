package com.technosignia.contractsystem.service;

import com.technosignia.contractsystem.dto.ContractRequest;
import com.technosignia.contractsystem.dto.DashboardStatsDto;
import com.technosignia.contractsystem.entity.Contract;
import com.technosignia.contractsystem.entity.ContractStatus;
import com.technosignia.contractsystem.entity.ModificationStatus;
import com.technosignia.contractsystem.entity.User;
import com.technosignia.contractsystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContractService {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ClauseRepository clauseRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private ModificationRequestRepository modificationRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private AuditService auditService;

    public List<Contract> searchAndFilter(String search, ContractStatus status) {
        String query = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        return contractRepository.searchAndFilterContracts(query, status);
    }

    public List<Contract> getAllContracts() {
        return contractRepository.findAll();
    }

    public Contract getContractById(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found with id: " + id));
    }

    @Transactional
    public Contract createContract(ContractRequest request) {
        if (contractRepository.existsByContractNumber(request.getContractNumber())) {
            throw new IllegalArgumentException("Contract number already exists: " + request.getContractNumber());
        }

        User currentUser = authService.getCurrentUser();

        Contract contract = new Contract(
                request.getContractNumber(),
                request.getTitle(),
                request.getDescription(),
                request.getStatus() != null ? request.getStatus() : ContractStatus.DRAFT,
                currentUser
        );

        Contract saved = contractRepository.save(contract);
        auditService.log("CONTRACT_CREATED", currentUser.getUsername(), "Contract", saved.getId(),
                "Created contract: " + saved.getTitle() + " (" + saved.getContractNumber() + ")");
        return saved;
    }

    @Transactional
    public Contract updateContract(Long id, ContractRequest request) {
        Contract contract = getContractById(id);

        if (!contract.getContractNumber().equals(request.getContractNumber()) &&
                contractRepository.existsByContractNumber(request.getContractNumber())) {
            throw new IllegalArgumentException("Contract number already in use: " + request.getContractNumber());
        }

        contract.setContractNumber(request.getContractNumber());
        contract.setTitle(request.getTitle());
        contract.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            contract.setStatus(request.getStatus());
        }

        Contract updated = contractRepository.save(contract);
        auditService.log("CONTRACT_UPDATED", null, "Contract", updated.getId(),
                "Updated contract details: " + updated.getTitle() + ", status: " + updated.getStatus());
        return updated;
    }

    @Transactional
    public void deleteContract(Long id) {
        Contract contract = getContractById(id);
        String number = contract.getContractNumber();
        String title = contract.getTitle();

        contractRepository.delete(contract);
        auditService.log("CONTRACT_DELETED", null, "Contract", id,
                "Deleted contract " + number + ": " + title);
    }

    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();
        List<Contract> all = contractRepository.findAll();
        stats.setTotalContracts(all.size());

        long active = 0, draft = 0, expired = 0, terminated = 0;
        for (Contract c : all) {
            if (c.getStatus() == ContractStatus.ACTIVE) active++;
            else if (c.getStatus() == ContractStatus.DRAFT) draft++;
            else if (c.getStatus() == ContractStatus.EXPIRED) expired++;
            else if (c.getStatus() == ContractStatus.TERMINATED) terminated++;
        }
        stats.setActiveContracts(active);
        stats.setDraftContracts(draft);
        stats.setExpiredContracts(expired);
        stats.setTerminatedContracts(terminated);

        stats.setPendingModifications(modificationRequestRepository.countByStatus(ModificationStatus.PENDING));
        stats.setApprovedModifications(modificationRequestRepository.countByStatus(ModificationStatus.APPROVED));
        stats.setRejectedModifications(modificationRequestRepository.countByStatus(ModificationStatus.REJECTED));

        stats.setTotalDocuments(documentRepository.count());
        stats.setTotalClauses(clauseRepository.count());
        stats.setTotalUsers(userRepository.count());

        return stats;
    }
}
