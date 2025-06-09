package com.pawsql.cache;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.pawsql.model.WorkspaceInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@State(name = "com.pawsql.WorkspaceCache", storages = @Storage("WorkspaceCache.xml"))
public class WorkspaceCache implements PersistentStateComponent<WorkspaceCache> {
    private static final long CACHE_DURATION = TimeUnit.MINUTES.toMillis(20);
    private static final WorkspaceCache INSTANCE = new WorkspaceCache();
    private List<WorkspaceInfo> workspaces;
    private long lastUpdateTime;

    private WorkspaceCache() {
    }

    public static WorkspaceCache getInstance() {
        return INSTANCE;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - lastUpdateTime > CACHE_DURATION;
    }

    public void updateCache(List<WorkspaceInfo> workspaces) {
        this.workspaces = workspaces;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public List<WorkspaceInfo> getWorkspaces() {
        return workspaces != null ? workspaces : Collections.emptyList();
    }

    public void clearCache() {
        this.workspaces = null;
        this.lastUpdateTime = 0;
    }

    @Nullable
    public WorkspaceCache getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull WorkspaceCache state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
