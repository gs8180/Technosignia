package com.technosignia.contractsystem.dto;

public class ApprovalRequest {
    private String comment;

    public ApprovalRequest() {
    }

    public ApprovalRequest(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
