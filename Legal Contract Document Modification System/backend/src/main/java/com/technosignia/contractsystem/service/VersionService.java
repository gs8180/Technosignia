package com.technosignia.contractsystem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.technosignia.contractsystem.entity.Clause;
import com.technosignia.contractsystem.entity.Contract;
import com.technosignia.contractsystem.entity.ContractVersion;
import com.technosignia.contractsystem.entity.User;
import com.technosignia.contractsystem.repository.ClauseRepository;
import com.technosignia.contractsystem.repository.ContractRepository;
import com.technosignia.contractsystem.repository.ContractVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VersionService {

    @Autowired
    private ContractVersionRepository contractVersionRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ClauseRepository clauseRepository;

    @Autowired
    private AuditService auditService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<ContractVersion> getVersionsForContract(Long contractId) {
        return contractVersionRepository.findByContractIdOrderByCreatedAtDesc(contractId);
    }

    public ContractVersion getVersionById(Long versionId) {
        return contractVersionRepository.findById(versionId)
                .orElseThrow(() -> new IllegalArgumentException("Version not found with id: " + versionId));
    }

    @Transactional
    public ContractVersion createVersionSnapshot(Contract contract, User changedBy, String changeSummary) {
        List<Clause> clauses = clauseRepository.findByContractIdOrderByClauseOrderAsc(contract.getId());

        List<Map<String, Object>> clauseSnapshots = clauses.stream().map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("title", c.getTitle());
            map.put("content", c.getContent());
            map.put("clauseOrder", c.getClauseOrder());
            return map;
        }).collect(Collectors.toList());

        String jsonSnapshot = "[]";
        try {
            jsonSnapshot = objectMapper.writeValueAsString(clauseSnapshots);
        } catch (JsonProcessingException e) {
            jsonSnapshot = "[]";
        }

        // Calculate next version: 1.0 -> 1.1 -> 1.2
        String currentVer = contract.getCurrentVersion();
        String nextVer = calculateNextVersion(currentVer);
        contract.setCurrentVersion(nextVer);
        contractRepository.save(contract);

        ContractVersion version = new ContractVersion(
                contract,
                nextVer,
                contract.getTitle(),
                contract.getDescription(),
                jsonSnapshot,
                changedBy,
                changeSummary
        );

        ContractVersion saved = contractVersionRepository.save(version);

        auditService.log("VERSION_CREATED", changedBy != null ? changedBy.getUsername() : "SYSTEM",
                "Contract", contract.getId(),
                "Created new contract version " + nextVer + " (" + changeSummary + ")");

        return saved;
    }

    private String calculateNextVersion(String current) {
        if (current == null || current.isBlank()) {
            return "1.0";
        }
        try {
            String[] parts = current.split("\\.");
            if (parts.length == 2) {
                int major = Integer.parseInt(parts[0]);
                int minor = Integer.parseInt(parts[1]);
                return major + "." + (minor + 1);
            }
        } catch (Exception e) {
            // fallback
        }
        return current + ".1";
    }
}
