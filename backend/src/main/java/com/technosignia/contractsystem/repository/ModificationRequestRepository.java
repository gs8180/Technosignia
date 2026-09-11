package com.technosignia.contractsystem.repository;

import com.technosignia.contractsystem.entity.ModificationRequest;
import com.technosignia.contractsystem.entity.ModificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModificationRequestRepository extends JpaRepository<ModificationRequest, Long> {
    List<ModificationRequest> findByContractIdOrderByRequestedAtDesc(Long contractId);
    List<ModificationRequest> findByStatusOrderByRequestedAtDesc(ModificationStatus status);
    List<ModificationRequest> findByRequestedByIdOrderByRequestedAtDesc(Long userId);
    long countByStatus(ModificationStatus status);
}
