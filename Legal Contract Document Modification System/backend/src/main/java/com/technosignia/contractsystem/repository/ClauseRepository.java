package com.technosignia.contractsystem.repository;

import com.technosignia.contractsystem.entity.Clause;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClauseRepository extends JpaRepository<Clause, Long> {
    List<Clause> findByContractIdOrderByClauseOrderAsc(Long contractId);
    void deleteByContractId(Long contractId);
}
