package com.pawsql.client;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.pawsql.client.api.ApiClient;
import com.pawsql.client.util.ConsoleLogUtils;
import com.pawsql.service.WorkspaceService;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class PluginSetup implements StartupActivity {
    private static final Logger LOG = Logger.getLogger(PluginSetup.class);

    @Override
    public void runActivity(@NotNull Project project) {
        ConsoleLogUtils.showLog(project);
        setup();
    }

    public void setup() {
        try {
            LOG.info("Initializing PawSQL plugin...");

            // 获取配置
            PawSettingState state = PawSettingState.getInstance();

            // 检查必要的配置
            if (state.getBaseUrl() == null || state.getBaseUrl().isEmpty()
                    || state.getEmail() == null || state.getEmail().isEmpty()) {
                LOG.warn("PawSQL Server is not configured");
                return;
            }
            // 初始化 PluginManager
            PluginManager pluginManager = PluginManager.getInstance();
            ApiClient apiClient = pluginManager.getApiClient();

            if (apiClient != null) {
                if (state.getUserKey() != null && !state.getUserKey().isEmpty())
                    apiClient.setUserKey(state.getUserKey());
                if (state.getFrontendUrl() != null && !state.getFrontendUrl().isEmpty())
                    apiClient.setFrontUrl(state.getFrontendUrl());
                WorkspaceService workspaceService = WorkspaceService.getInstance(apiClient);
                workspaceService.getWorkspaces();
            }

            LOG.info("PawSQL plugin initialized successfully");

        } catch (Exception e) {
            LOG.error("Failed to initialize PawSQL plugin", e);
        }
    }
}
