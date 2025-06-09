package com.pawsql.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.pawsql.client.util.ConsoleLogUtils;
import com.pawsql.model.WorkspaceInfo;
import com.pawsql.service.OptimizationService;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SelectOptimizeAction extends AnAction {
    private final WorkspaceInfo workspace;

    public SelectOptimizeAction(WorkspaceInfo workspace) {
        super(workspace.getWorkspaceName(), workspace.getWorkspaceName(), workspace.getIcon()); // 动态设置动作的名称
        this.workspace = workspace;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            Messages.showMessageDialog("Project is not available.", "Error", Messages.getErrorIcon());
            return;
        }
        ConsoleLogUtils.showLog(project);
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) return;
        SelectionModel selectionModel = editor.getSelectionModel();
        String selectedText = selectionModel.getSelectedText();
        if (selectedText == null || selectedText.trim().isEmpty()) {
            Messages.showWarningDialog(
                    project,
                    "Please select a SQL query to optimize!",
                    "Warning"
            );
            return;
        }

        if (workspace == null) {
            Messages.showErrorDialog(
                    project,
                    "No Workspace selected",
                    "Error"
            );
            return;
        }

        // 获取当前文件路径
        String filePath = Objects.requireNonNull(e.getData(CommonDataKeys.VIRTUAL_FILE)).getPath();

        // 执行优化
        OptimizationService service = OptimizationService.getInstance(project);
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "PawSQL") {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                // start your process
                showProcess(progressIndicator, 0.15, "Connecting to PawSQL server and optimizing SQL...");
                service.optimizeSQL(filePath, selectedText.trim(), workspace);

            }

            private void showProcess(@NotNull ProgressIndicator progressIndicator, double fraction, String info2) {
                String info = "PawSQL progress " + fraction * 100 + "%," + info2;
                progressIndicator.setFraction(fraction);
                progressIndicator.setText(info);
            }
        });
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setText(workspace.toString());
        e.getPresentation().setEnabledAndVisible(true);
    }
}
