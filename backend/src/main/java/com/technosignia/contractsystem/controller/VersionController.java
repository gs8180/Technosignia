package com.technosignia.contractsystem.controller;

import com.technosignia.contractsystem.entity.ContractVersion;
import com.technosignia.contractsystem.service.VersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/contracts/{contractId}/versions")
public class VersionController {

    @Autowired
    private VersionService versionService;

    @GetMapping
    public ResponseEntity<List<ContractVersion>> getVersions(@PathVariable Long contractId) {
        return ResponseEntity.ok(versionService.getVersionsForContract(contractId));
    }

    @GetMapping("/{versionId}")
    public ResponseEntity<ContractVersion> getVersionById(
            @PathVariable Long contractId,
            @PathVariable Long versionId) {
        return ResponseEntity.ok(versionService.getVersionById(versionId));
    }
}
