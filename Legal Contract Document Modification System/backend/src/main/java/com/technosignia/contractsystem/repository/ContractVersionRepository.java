package com.technosignia.contractsystem.repository;

import com.technosignia.contractsystem.entity.ContractVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractVersionRepository extends JpaRepository<ContractVersion, Long> {
    List<ContractVersion> findByContractIdOrderByCreatedAtDesc(Long contractId);
}
