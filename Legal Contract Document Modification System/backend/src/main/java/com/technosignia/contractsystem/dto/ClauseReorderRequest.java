package com.technosignia.contractsystem.dto;

import java.util.List;

public class ClauseReorderRequest {
    private List<Long> clauseIds;

    public ClauseReorderRequest() {
    }

    public ClauseReorderRequest(List<Long> clauseIds) {
        this.clauseIds = clauseIds;
    }

    public List<Long> getClauseIds() {
        return clauseIds;
    }

    public void setClauseIds(List<Long> clauseIds) {
        this.clauseIds = clauseIds;
    }
}
