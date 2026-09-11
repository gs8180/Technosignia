package com.technosignia.contractsystem.repository;

import com.technosignia.contractsystem.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByContractIdOrderByUploadedAtDesc(Long contractId);
}
