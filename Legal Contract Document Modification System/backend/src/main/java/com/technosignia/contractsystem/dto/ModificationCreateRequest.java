package com.technosignia.contractsystem.dto;

import com.technosignia.contractsystem.entity.ModificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ModificationCreateRequest {
    @NotNull
    private Long contractId;

    private Long clauseId;

    @NotNull
    private ModificationType requestType;

    @NotBlank
    private String proposedValue;

    @NotBlank
    private String reason;

    public ModificationCreateRequest() {
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getClauseId() {
        return clauseId;
    }

    public void setClauseId(Long clauseId) {
        this.clauseId = clauseId;
    }

    public ModificationType getRequestType() {
        return requestType;
    }

    public void setRequestType(ModificationType requestType) {
        this.requestType = requestType;
    }

    public String getProposedValue() {
        return proposedValue;
    }

    public void setProposedValue(String proposedValue) {
        this.proposedValue = proposedValue;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
