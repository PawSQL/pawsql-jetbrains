package com.pawsql.service;

import com.intellij.notification.Notification;
import com.intellij.openapi.project.Project;
import com.pawsql.cache.WorkspaceCache;
import com.pawsql.client.api.ApiClient;
import com.pawsql.client.api.ApiResult;
import com.pawsql.exception.OptimizationException;
import com.pawsql.exception.PawSqlException;
import com.pawsql.model.WorkspaceInfo;
import com.pawsql.notification.OptimizationNotifier;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WorkspaceService {
    private static final org.apache.log4j.Logger LOG = Logger.getLogger(WorkspaceService.class);
    private final Project project;
    private ApiClient apiClient;
    private final WorkspaceCache workspaceCache;
    private static WorkspaceService instance;

    public WorkspaceService(ApiClient apiClient) {
        project = null;
        this.workspaceCache = WorkspaceCache.getInstance();
        this.apiClient = apiClient;
    }

    public static synchronized WorkspaceService getInstance(ApiClient apiClient) {
        if (instance == null) {
            instance = new WorkspaceService(apiClient);
        }
        instance.apiClient = apiClient;
        return instance;
    }

    public List<WorkspaceInfo> getWorkspaces() throws IOException {
        List<WorkspaceInfo> cachedWorkspaces = workspaceCache.getWorkspaces();
        if (!cachedWorkspaces.isEmpty() && !workspaceCache.isExpired()) {
            LOG.debug("Using cached workspaces, size: " + cachedWorkspaces.size());
            return cachedWorkspaces;
        }
        return refreshWorkspaces();
    }

    public List<WorkspaceInfo> refreshWorkspaces() {
        Notification loadingNotification = null;
        try {
            // 显示加载通知
            loadingNotification = OptimizationNotifier.notifyInfo(
                    project,
                    "PawSQL Workspaces",
                    "Listing all workspaces..."
            );
            if (apiClient == null) {
                throw new PawSqlException("API client is not initialized");
            }

            ApiResult result = apiClient.listWorkspaces();
            if (result.getCode() != 200 || result.getData() == null) {
                String message = result.getMessage();
                if (message == null || message.trim().isEmpty()) {
                    message = "Please make sure your IDE is connected to PawSQL Server!";
                }
                throw new PawSqlException("Listing workspaces fails " + message);
            }

            List<WorkspaceInfo> workspaceInfos = new ArrayList<>();
            Map<String, Object> pageData = (Map<String, Object>) result.getData();

            // 从IPage中获取records列表
            List<Map<String, Object>> records = (List<Map<String, Object>>) pageData.get("records");
            if (records != null) {
                workspaceInfos = records.stream()
                        .map(workspace -> {
                            String workspaceName = String.valueOf(workspace.get("workspaceName"));
                            WorkspaceInfo info = new WorkspaceInfo(workspaceName);
                            info.setWorkspaceId(String.valueOf(workspace.get("workspaceId")));
                            info.setWorkspaceDefinitionId(String.valueOf(workspace.get("workspaceDefinitionId")));
                            return info;
                        })
                        .collect(Collectors.toList());
            }
            workspaceCache.updateCache(workspaceInfos);
            LOG.info("Workspace cache updated, size: " + workspaceInfos.size());

            // 关闭加载通知并显示成功通知
            loadingNotification.expire();
            OptimizationNotifier.notifyInfo(project, "PawSQL Workspace", "Workspace List Refreshed!");

            return workspaceInfos;
        } catch (Exception e) {
            LOG.error("Failed to refresh workspaces", e);
            // 确保在发生错误时也关闭加载通知
            if (loadingNotification != null) {
                loadingNotification.expire();
            }
            // 抛出异常前显示错误通知
            OptimizationNotifier.notifyError(project, e.getMessage());
            throw new PawSqlException(e.getMessage(), e);
        }
    }

    public WorkspaceInfo getWorkspaceById(String id) throws OptimizationException {
        try {
            List<WorkspaceInfo> workspaces = getWorkspaces();
            return workspaces.stream()
                    .filter(w -> w.getWorkspaceId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new OptimizationException("Workspace not found: " + id));
        } catch (IOException e) {
            throw new OptimizationException("Failed to get workspace: " + e.getMessage());
        }
    }

    public String getCreateWorkspaceUrl() {
        if (apiClient == null) {
            throw new IllegalStateException("API client is not initialized");
        }
        return apiClient.getBaseUrl() + "/workspace/create";
    }

    public List<WorkspaceInfo> getCachedWorkspaces() {
        return workspaceCache.getWorkspaces();
    }
}
