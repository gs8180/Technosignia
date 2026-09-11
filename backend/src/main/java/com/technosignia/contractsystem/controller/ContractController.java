package com.technosignia.contractsystem.controller;

import com.technosignia.contractsystem.dto.ContractRequest;
import com.technosignia.contractsystem.dto.DashboardStatsDto;
import com.technosignia.contractsystem.entity.Contract;
import com.technosignia.contractsystem.entity.ContractStatus;
import com.technosignia.contractsystem.service.ContractService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {

    @Autowired
    private ContractService contractService;

    @GetMapping
    public ResponseEntity<List<Contract>> getContracts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) ContractStatus status) {
        return ResponseEntity.ok(contractService.searchAndFilter(search, status));
    }

    @GetMapping("/dashboard-stats")
    public ResponseEntity<DashboardStatsDto> getDashboardStats() {
        return ResponseEntity.ok(contractService.getDashboardStats());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contract> getContractById(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.getContractById(id));
    }

    @PostMapping
    public ResponseEntity<Contract> createContract(@Valid @RequestBody ContractRequest request) {
        return ResponseEntity.ok(contractService.createContract(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contract> updateContract(@PathVariable Long id, @Valid @RequestBody ContractRequest request) {
        return ResponseEntity.ok(contractService.updateContract(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteContract(@PathVariable Long id) {
        contractService.deleteContract(id);
        return ResponseEntity.ok(Map.of("message", "Contract deleted successfully"));
    }
}
