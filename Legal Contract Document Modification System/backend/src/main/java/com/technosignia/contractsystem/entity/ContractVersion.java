package com.technosignia.contractsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contract_versions")
public class ContractVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Contract contract;

    @Column(name = "version_number", nullable = false, length = 20)
    private String versionNumber;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "clauses_snapshot_json", columnDefinition = "LONGTEXT")
    private String clausesSnapshotJson;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "changed_by_user_id")
    private User changedBy;

    @Column(name = "change_summary", columnDefinition = "TEXT")
    private String changeSummary;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ContractVersion() {
    }

    public ContractVersion(Contract contract, String versionNumber, String title, String description, String clausesSnapshotJson, User changedBy, String changeSummary) {
        this.contract = contract;
        this.versionNumber = versionNumber;
        this.title = title;
        this.description = description;
        this.clausesSnapshotJson = clausesSnapshotJson;
        this.changedBy = changedBy;
        this.changeSummary = changeSummary;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClausesSnapshotJson() {
        return clausesSnapshotJson;
    }

    public void setClausesSnapshotJson(String clausesSnapshotJson) {
        this.clausesSnapshotJson = clausesSnapshotJson;
    }

    public User getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(User changedBy) {
        this.changedBy = changedBy;
    }

    public String getChangeSummary() {
        return changeSummary;
    }

    public void setChangeSummary(String changeSummary) {
        this.changeSummary = changeSummary;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
