package com.technosignia.contractsystem.controller;

import com.technosignia.contractsystem.dto.ApprovalRequest;
import com.technosignia.contractsystem.dto.ModificationCreateRequest;
import com.technosignia.contractsystem.dto.RejectionRequest;
import com.technosignia.contractsystem.entity.ModificationRequest;
import com.technosignia.contractsystem.service.ModificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modifications")
public class ModificationController {

    @Autowired
    private ModificationService modificationService;

    @GetMapping
    public ResponseEntity<List<ModificationRequest>> getAllModifications() {
        return ResponseEntity.ok(modificationService.getAllModifications());
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('APPROVER', 'ADMIN')")
    public ResponseEntity<List<ModificationRequest>> getPendingModifications() {
        return ResponseEntity.ok(modificationService.getPendingModifications());
    }

    @GetMapping("/my-requests")
    public ResponseEntity<List<ModificationRequest>> getMyModifications() {
        return ResponseEntity.ok(modificationService.getModificationsByCurrentUser());
    }

    @GetMapping("/contract/{contractId}")
    public ResponseEntity<List<ModificationRequest>> getModificationsForContract(@PathVariable Long contractId) {
        return ResponseEntity.ok(modificationService.getModificationsForContract(contractId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModificationRequest> getModificationById(@PathVariable Long id) {
        return ResponseEntity.ok(modificationService.getModificationById(id));
    }

    @PostMapping
    public ResponseEntity<ModificationRequest> createModification(@Valid @RequestBody ModificationCreateRequest request) {
        return ResponseEntity.ok(modificationService.createModificationRequest(request));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('APPROVER', 'ADMIN')")
    public ResponseEntity<ModificationRequest> approveModification(
            @PathVariable Long id,
            @RequestBody(required = false) ApprovalRequest request) {
        return ResponseEntity.ok(modificationService.approveModification(id, request));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('APPROVER', 'ADMIN')")
    public ResponseEntity<ModificationRequest> rejectModification(
            @PathVariable Long id,
            @Valid @RequestBody RejectionRequest request) {
        return ResponseEntity.ok(modificationService.rejectModification(id, request));
    }
}
