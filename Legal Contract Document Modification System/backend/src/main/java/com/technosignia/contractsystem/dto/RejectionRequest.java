package com.technosignia.contractsystem.dto;

import jakarta.validation.constraints.NotBlank;

public class RejectionRequest {
    @NotBlank
    private String rejectionReason;

    public RejectionRequest() {
    }

    public RejectionRequest(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
