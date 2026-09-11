package com.technosignia.contractsystem.repository;

import com.technosignia.contractsystem.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findAllByOrderByTimestampDesc();
    List<AuditLog> findTop50ByOrderByTimestampDesc();
    List<AuditLog> findByEntityNameAndEntityIdOrderByTimestampDesc(String entityName, Long entityId);
}
