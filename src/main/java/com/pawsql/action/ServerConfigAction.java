package com.pawsql.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.pawsql.client.PawSettingPage;
import com.pawsql.client.util.ConsoleLogUtils;
import org.jetbrains.annotations.NotNull;

public class ServerConfigAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;
        ConsoleLogUtils.showLog(project);
        ShowSettingsUtil.getInstance().showSettingsDialog(project, PawSettingPage.class);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        // 只要有项目就启用
        e.getPresentation().setEnabled(e.getProject() != null);
        e.getPresentation().setText("PawSQL Config...");
        e.getPresentation().setEnabledAndVisible(true);
    }
}
