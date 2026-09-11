package com.technosignia.contractsystem.service;

import com.technosignia.contractsystem.entity.AuditLog;
import com.technosignia.contractsystem.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    public void log(String action, String performedBy, String entityName, Long entityId, String details) {
        String actor = performedBy;
        if (actor == null || actor.isBlank()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                actor = auth.getName();
            } else {
                actor = "SYSTEM";
            }
        }

        AuditLog log = new AuditLog(action, actor, entityName, entityId, details);
        auditLogRepository.save(log);
    }

    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findTop50ByOrderByTimestampDesc();
    }

    public List<AuditLog> getLogsForEntity(String entityName, Long entityId) {
        return auditLogRepository.findByEntityNameAndEntityIdOrderByTimestampDesc(entityName, entityId);
    }
}
