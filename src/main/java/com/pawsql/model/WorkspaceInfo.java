package com.pawsql.model;

import com.google.gson.annotations.SerializedName;
import com.pawsql.client.icons.PawSQLIcons;

import javax.swing.*;

public class WorkspaceInfo {
    @SerializedName("workspaceId")
    private String workspaceId;

    @SerializedName("workspaceName")
    private String workspaceName;

    @SerializedName("workspaceDefinitionId")
    private String workspaceDefinitionId;

    private String dbHost;
    private String dbPort;
    private String dbType;
    private String createTime;
    private Integer numberOfAnalysis;
    private String latestAnalysisTime;
    private String latestAuditTime;
    private Integer numberOfAudit;
    private String status;

    public WorkspaceInfo(String name) {
        workspaceId = null;
        workspaceName = name;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public String getWorkspaceDefinitionId() {
        return workspaceDefinitionId;
    }

    public void setWorkspaceDefinitionId(String workspaceDefinitionId) {
        this.workspaceDefinitionId = workspaceDefinitionId;
    }

    public String getDbHost() {
        return dbHost;
    }

    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
    }

    public String getDbPort() {
        return dbPort;
    }

    public void setDbPort(String dbPort) {
        this.dbPort = dbPort;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getNumberOfAnalysis() {
        return numberOfAnalysis;
    }

    public void setNumberOfAnalysis(Integer numberOfAnalysis) {
        this.numberOfAnalysis = numberOfAnalysis;
    }

    public String getLatestAnalysisTime() {
        return latestAnalysisTime;
    }

    public void setLatestAnalysisTime(String latestAnalysisTime) {
        this.latestAnalysisTime = latestAnalysisTime;
    }

    public String getLatestAuditTime() {
        return latestAuditTime;
    }

    public void setLatestAuditTime(String latestAuditTime) {
        this.latestAuditTime = latestAuditTime;
    }

    public Integer getNumberOfAudit() {
        return numberOfAudit;
    }

    public void setNumberOfAudit(Integer numberOfAudit) {
        this.numberOfAudit = numberOfAudit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return workspaceName;
    }

    public Icon getIcon() {
        if (workspaceDefinitionId != null) {
            Icon icon = PawSQLIcons.getIcon(workspaceDefinitionId);
            if (icon == null)
                return PawSQLIcons.getIcon("default-db-online");
            else
                return icon;
        }
        return null;
    }
}
