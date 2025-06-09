package com.pawsql.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.diagnostic.Logger;
import com.pawsql.cache.WorkspaceCache;
import com.pawsql.client.PawSettingState;
import com.pawsql.client.PluginManager;
import com.pawsql.model.WorkspaceInfo;

public class SelectOptimizeActionGroup extends DefaultActionGroup {
    private final WorkspaceCache workspaceCache;
    private static final Logger LOG = Logger.getInstance(SelectOptimizeActionGroup.class);
    private final PawSettingState settings = PawSettingState.getInstance();

    public SelectOptimizeActionGroup() {
        super();
        this.workspaceCache = WorkspaceCache.getInstance();
        if (workspaceCache == null) {
            throw new IllegalStateException("WorkSpaceManager instance is null!");
        }
        LOG.info("--SelectOptimizeActionGroup initialized " + workspaceCache.getWorkspaces().size());

        boolean isEnabled = PluginManager.getInstance().isApiClientInitialized();
        if (isEnabled) {
            for (WorkspaceInfo workspace : workspaceCache.getWorkspaces()) {
                add(new SelectOptimizeAction(workspace));
            }
            add(new MoreOptimizeAction());
        }
        add(new ServerConfigAction());
    }

    @Override
    public void update(AnActionEvent e) {
        LOG.info("--SelectOptimizeActionGroup updated " + workspaceCache.getWorkspaces().size());
        super.update(e);
        reinit();
    }

    private void reinit() {
        removeAll(); // 清除现有的动作
        boolean isEnabled = PluginManager.getInstance().isApiClientInitialized();
        if (isEnabled) {
            for (WorkspaceInfo workspace : workspaceCache.getWorkspaces()) {
                add(new SelectOptimizeAction(workspace));
            }
            add(new MoreOptimizeAction());
        }
        add(new ServerConfigAction());
    }
}
