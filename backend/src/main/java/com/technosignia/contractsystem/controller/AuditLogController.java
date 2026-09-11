package com.technosignia.contractsystem.controller;

import com.technosignia.contractsystem.entity.AuditLog;
import com.technosignia.contractsystem.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    @Autowired
    private AuditService auditService;

    @GetMapping
    public ResponseEntity<List<AuditLog>> getAuditLogs(
            @RequestParam(required = false) String entityName,
            @RequestParam(required = false) Long entityId) {
        if (entityName != null && entityId != null) {
            return ResponseEntity.ok(auditService.getLogsForEntity(entityName, entityId));
        }
        return ResponseEntity.ok(auditService.getAllLogs());
    }
}
