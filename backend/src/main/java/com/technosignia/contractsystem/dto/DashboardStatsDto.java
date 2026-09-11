package com.technosignia.contractsystem.dto;

public class DashboardStatsDto {
    private long totalContracts;
    private long activeContracts;
    private long draftContracts;
    private long expiredContracts;
    private long terminatedContracts;
    private long pendingModifications;
    private long approvedModifications;
    private long rejectedModifications;
    private long totalDocuments;
    private long totalClauses;
    private long totalUsers;

    public DashboardStatsDto() {
    }

    public long getTotalContracts() {
        return totalContracts;
    }

    public void setTotalContracts(long totalContracts) {
        this.totalContracts = totalContracts;
    }

    public long getActiveContracts() {
        return activeContracts;
    }

    public void setActiveContracts(long activeContracts) {
        this.activeContracts = activeContracts;
    }

    public long getDraftContracts() {
        return draftContracts;
    }

    public void setDraftContracts(long draftContracts) {
        this.draftContracts = draftContracts;
    }

    public long getExpiredContracts() {
        return expiredContracts;
    }

    public void setExpiredContracts(long expiredContracts) {
        this.expiredContracts = expiredContracts;
    }

    public long getTerminatedContracts() {
        return terminatedContracts;
    }

    public void setTerminatedContracts(long terminatedContracts) {
        this.terminatedContracts = terminatedContracts;
    }

    public long getPendingModifications() {
        return pendingModifications;
    }

    public void setPendingModifications(long pendingModifications) {
        this.pendingModifications = pendingModifications;
    }

    public long getApprovedModifications() {
        return approvedModifications;
    }

    public void setApprovedModifications(long approvedModifications) {
        this.approvedModifications = approvedModifications;
    }

    public long getRejectedModifications() {
        return rejectedModifications;
    }

    public void setRejectedModifications(long rejectedModifications) {
        this.rejectedModifications = rejectedModifications;
    }

    public long getTotalDocuments() {
        return totalDocuments;
    }

    public void setTotalDocuments(long totalDocuments) {
        this.totalDocuments = totalDocuments;
    }

    public long getTotalClauses() {
        return totalClauses;
    }

    public void setTotalClauses(long totalClauses) {
        this.totalClauses = totalClauses;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }
}
