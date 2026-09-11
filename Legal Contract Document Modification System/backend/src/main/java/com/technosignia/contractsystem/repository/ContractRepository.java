package com.technosignia.contractsystem.repository;

import com.technosignia.contractsystem.entity.Contract;
import com.technosignia.contractsystem.entity.ContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    Optional<Contract> findByContractNumber(String contractNumber);
    boolean existsByContractNumber(String contractNumber);

    List<Contract> findByStatusOrderByCreatedAtDesc(ContractStatus status);

    @Query("SELECT c FROM Contract c WHERE " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:search IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.contractNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY c.createdAt DESC")
    List<Contract> searchAndFilterContracts(@Param("search") String search, @Param("status") ContractStatus status);
}
