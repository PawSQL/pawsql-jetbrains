package com.pawsql.client.api.model;

public class ApiAnalysisCreate {
    private String userKey;
    private String workspace;
    private String workload;
    private String dbType;
    private Boolean singleQueryFlag;
    private String queryMode;
    private Boolean validateFlag;
    private String analysisName;
    private Boolean analyzeFlag;
    private Boolean deduplicateFlag;
    private Boolean updateStatsBeforeValidationFlag;
    private Boolean indexOnly;
    private Integer maxMembersForIndexOnly;
    private Integer maxMembers;
    private Integer maxPerTable;
    private Integer maxSpace;
    private Boolean closeRewrite;

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

    public String getWorkload() {
        return workload;
    }

    public void setWorkload(String workload) {
        this.workload = workload;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public Boolean getSingleQueryFlag() {
        return singleQueryFlag;
    }

    public void setSingleQueryFlag(Boolean singleQueryFlag) {
        this.singleQueryFlag = singleQueryFlag;
    }

    public String getQueryMode() {
        return queryMode;
    }

    public void setQueryMode(String queryMode) {
        this.queryMode = queryMode;
    }

    public Boolean getValidateFlag() {
        return validateFlag;
    }

    public void setValidateFlag(Boolean validateFlag) {
        this.validateFlag = validateFlag;
    }

    public String getAnalysisName() {
        return analysisName;
    }

    public void setAnalysisName(String analysisName) {
        this.analysisName = analysisName;
    }

    public Boolean getAnalyzeFlag() {
        return analyzeFlag;
    }

    public void setAnalyzeFlag(Boolean analyzeFlag) {
        this.analyzeFlag = analyzeFlag;
    }

    public Boolean getDeduplicateFlag() {
        return deduplicateFlag;
    }

    public void setDeduplicateFlag(Boolean deduplicateFlag) {
        this.deduplicateFlag = deduplicateFlag;
    }

    public Boolean getUpdateStatsBeforeValidationFlag() {
        return updateStatsBeforeValidationFlag;
    }

    public void setUpdateStatsBeforeValidationFlag(Boolean updateStatsBeforeValidationFlag) {
        this.updateStatsBeforeValidationFlag = updateStatsBeforeValidationFlag;
    }

    public Boolean getIndexOnly() {
        return indexOnly;
    }

    public void setIndexOnly(Boolean indexOnly) {
        this.indexOnly = indexOnly;
    }

    public Integer getMaxMembersForIndexOnly() {
        return maxMembersForIndexOnly;
    }

    public void setMaxMembersForIndexOnly(Integer maxMembersForIndexOnly) {
        this.maxMembersForIndexOnly = maxMembersForIndexOnly;
    }

    public Integer getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(Integer maxMembers) {
        this.maxMembers = maxMembers;
    }

    public Integer getMaxPerTable() {
        return maxPerTable;
    }

    public void setMaxPerTable(Integer maxPerTable) {
        this.maxPerTable = maxPerTable;
    }

    public Integer getMaxSpace() {
        return maxSpace;
    }

    public void setMaxSpace(Integer maxSpace) {
        this.maxSpace = maxSpace;
    }

    public Boolean getCloseRewrite() {
        return closeRewrite;
    }

    public void setCloseRewrite(Boolean closeRewrite) {
        this.closeRewrite = closeRewrite;
    }
}
