package com.technosignia.contractsystem.controller;

import com.technosignia.contractsystem.dto.ClauseReorderRequest;
import com.technosignia.contractsystem.dto.ClauseRequest;
import com.technosignia.contractsystem.entity.Clause;
import com.technosignia.contractsystem.service.ClauseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contracts/{contractId}/clauses")
public class ClauseController {

    @Autowired
    private ClauseService clauseService;

    @GetMapping
    public ResponseEntity<List<Clause>> getClauses(@PathVariable Long contractId) {
        return ResponseEntity.ok(clauseService.getClausesByContractId(contractId));
    }

    @PostMapping
    public ResponseEntity<Clause> addClause(
            @PathVariable Long contractId,
            @Valid @RequestBody ClauseRequest request) {
        return ResponseEntity.ok(clauseService.addClause(contractId, request));
    }

    @PutMapping("/{clauseId}")
    public ResponseEntity<Clause> updateClause(
            @PathVariable Long contractId,
            @PathVariable Long clauseId,
            @Valid @RequestBody ClauseRequest request) {
        return ResponseEntity.ok(clauseService.updateClause(clauseId, request));
    }

    @DeleteMapping("/{clauseId}")
    public ResponseEntity<Map<String, String>> deleteClause(
            @PathVariable Long contractId,
            @PathVariable Long clauseId) {
        clauseService.deleteClause(clauseId);
        return ResponseEntity.ok(Map.of("message", "Clause deleted successfully"));
    }

    @PutMapping("/reorder")
    public ResponseEntity<List<Clause>> reorderClauses(
            @PathVariable Long contractId,
            @RequestBody ClauseReorderRequest reorderRequest) {
        return ResponseEntity.ok(clauseService.reorderClauses(contractId, reorderRequest));
    }
}
