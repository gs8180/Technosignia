package com.technosignia.contractsystem.dto;

import jakarta.validation.constraints.NotBlank;

public class ClauseRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private Integer clauseOrder;

    public ClauseRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getClauseOrder() {
        return clauseOrder;
    }

    public void setClauseOrder(Integer clauseOrder) {
        this.clauseOrder = clauseOrder;
    }
}
