package com.pawsql.client;

import com.pawsql.client.api.ApiClient;

public class PluginManager {
    private static PluginManager instance;
    private ApiClient apiClient;

    private PluginManager() {
    }

    public static synchronized PluginManager getInstance() {
        if (instance == null) {
            instance = new PluginManager();
        }
        return instance;
    }

    public ApiClient getApiClient() {
        if (apiClient == null) {
            PawSettingState settings = PawSettingState.getInstance();
            apiClient = new ApiClient(
                    settings.getBaseUrl(),
                    settings.getEmail(),
                    settings.getPassword()
            );
            apiClient.setUserKey(settings.getUserKey());
        }
        return apiClient;
    }

    public void updateApiClient(ApiClient api) {
        apiClient = api;
    }

    public boolean isApiClientInitialized() {
        return apiClient != null && apiClient.getUserKey() != null && !apiClient.getUserKey().isEmpty();
    }
}
