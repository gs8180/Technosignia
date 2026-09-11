package com.technosignia.contractsystem.service;

import com.technosignia.contractsystem.dto.ClauseReorderRequest;
import com.technosignia.contractsystem.dto.ClauseRequest;
import com.technosignia.contractsystem.entity.Clause;
import com.technosignia.contractsystem.entity.Contract;
import com.technosignia.contractsystem.repository.ClauseRepository;
import com.technosignia.contractsystem.repository.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClauseService {

    @Autowired
    private ClauseRepository clauseRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private AuditService auditService;

    public List<Clause> getClausesByContractId(Long contractId) {
        return clauseRepository.findByContractIdOrderByClauseOrderAsc(contractId);
    }

    public Clause getClauseById(Long clauseId) {
        return clauseRepository.findById(clauseId)
                .orElseThrow(() -> new IllegalArgumentException("Clause not found with id: " + clauseId));
    }

    @Transactional
    public Clause addClause(Long contractId, ClauseRequest request) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found with id: " + contractId));

        List<Clause> existing = clauseRepository.findByContractIdOrderByClauseOrderAsc(contractId);
        int nextOrder = request.getClauseOrder() != null ? request.getClauseOrder() : (existing.size() + 1);

        Clause clause = new Clause(request.getTitle(), request.getContent(), nextOrder, contract);
        Clause saved = clauseRepository.save(clause);

        auditService.log("CLAUSE_CREATED", null, "Contract", contractId,
                "Added clause: '" + saved.getTitle() + "' at position " + saved.getClauseOrder());

        return saved;
    }

    @Transactional
    public Clause updateClause(Long clauseId, ClauseRequest request) {
        Clause clause = getClauseById(clauseId);

        clause.setTitle(request.getTitle());
        clause.setContent(request.getContent());
        if (request.getClauseOrder() != null) {
            clause.setClauseOrder(request.getClauseOrder());
        }

        Clause updated = clauseRepository.save(clause);

        auditService.log("CLAUSE_UPDATED", null, "Contract", clause.getContract().getId(),
                "Updated clause '" + updated.getTitle() + "'");

        return updated;
    }

    @Transactional
    public void deleteClause(Long clauseId) {
        Clause clause = getClauseById(clauseId);
        Long contractId = clause.getContract().getId();
        String title = clause.getTitle();

        clauseRepository.delete(clause);

        // Normalize ordering of remaining clauses
        List<Clause> remaining = clauseRepository.findByContractIdOrderByClauseOrderAsc(contractId);
        for (int i = 0; i < remaining.size(); i++) {
            remaining.get(i).setClauseOrder(i + 1);
        }
        clauseRepository.saveAll(remaining);

        auditService.log("CLAUSE_DELETED", null, "Contract", contractId,
                "Deleted clause: '" + title + "'");
    }

    @Transactional
    public List<Clause> reorderClauses(Long contractId, ClauseReorderRequest reorderRequest) {
        List<Long> orderedIds = reorderRequest.getClauseIds();
        List<Clause> clauses = clauseRepository.findByContractIdOrderByClauseOrderAsc(contractId);

        for (int i = 0; i < orderedIds.size(); i++) {
            Long targetId = orderedIds.get(i);
            int newOrder = i + 1;
            for (Clause c : clauses) {
                if (c.getId().equals(targetId)) {
                    c.setClauseOrder(newOrder);
                    break;
                }
            }
        }

        clauseRepository.saveAll(clauses);

        auditService.log("CLAUSES_REORDERED", null, "Contract", contractId,
                "Reordered " + orderedIds.size() + " clauses in contract");

        return clauseRepository.findByContractIdOrderByClauseOrderAsc(contractId);
    }
}
