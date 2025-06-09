package com.pawsql.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.pawsql.client.PawSettingPage;
import com.pawsql.client.PluginManager;
import com.pawsql.client.util.ConsoleLogUtils;
import com.pawsql.model.WorkspaceInfo;
import com.pawsql.service.OptimizationService;
import com.pawsql.ui.WorkspaceDialog;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MoreOptimizeAction extends AnAction {
    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);

        // 检查配置是否有效
        boolean isEnabled = project != null && editor != null && PluginManager.getInstance().isApiClientInitialized();

        if (!isEnabled) {
            String tooltip = "Setup PawSQL server first!";
            e.getPresentation().setDescription(tooltip);
            ShowSettingsUtil.getInstance().showSettingsDialog(project, PawSettingPage.class);
        } else {
            e.getPresentation().setText("More Workspaces...");
        }

        e.getPresentation().setEnabled(isEnabled);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;
        ConsoleLogUtils.showLog(project);
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) return;

        SelectionModel selectionModel = editor.getSelectionModel();
        String selectedText = selectionModel.getSelectedText();
        if (selectedText == null || selectedText.trim().isEmpty()) {
            Messages.showWarningDialog(
                    project,
                    "Please select the SQL query to optimize!",
                    "Warning"
            );
            return;
        }

        // 选择工作空间
        WorkspaceDialog dialog = new WorkspaceDialog(project);
        if (!dialog.showAndGet()) {
            return;
        }

        WorkspaceInfo workspace = dialog.getSelectedWorkspace();
        if (workspace == null) {
            Messages.showErrorDialog(
                    project,
                    "No Workspace Selected",
                    "Error"
            );
            return;
        }

        // 获取当前文件路径
        String filePath = Objects.requireNonNull(e.getData(CommonDataKeys.VIRTUAL_FILE)).getPath();

        // 执行优化
        OptimizationService service = OptimizationService.getInstance(project);
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Connecting to PawSQL Server to optimize SQL...") {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                // start your process
                showProcess(progressIndicator, 0.1, "Optimizing SQL on PawSQL Server.");
                service.optimizeSQL(filePath, selectedText.trim(), workspace);
            }

            private void showProcess(@NotNull ProgressIndicator progressIndicator, double fraction, String info2) {
                String info = "PawSQL optimization progress " + fraction * 100 + "%," + info2;
                progressIndicator.setFraction(fraction);
                progressIndicator.setText(info);
            }
        });
    }
}
