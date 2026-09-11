package com.technosignia.contractsystem.dto;

import com.technosignia.contractsystem.entity.ContractStatus;
import jakarta.validation.constraints.NotBlank;

public class ContractRequest {
    @NotBlank
    private String contractNumber;

    @NotBlank
    private String title;

    private String description;

    private ContractStatus status = ContractStatus.DRAFT;

    public ContractRequest() {
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
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

    public ContractStatus getStatus() {
        return status;
    }

    public void setStatus(ContractStatus status) {
        this.status = status;
    }
}
