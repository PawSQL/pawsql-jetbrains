package com.pawsql.ui;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.pawsql.client.PawSettingPage;
import com.pawsql.client.PawSettingState;
import com.pawsql.client.PluginManager;
import com.pawsql.client.api.ApiClient;
import com.pawsql.model.WorkspaceInfo;
import com.pawsql.notification.OptimizationNotifier;
import com.pawsql.service.WorkspaceService;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class WorkspaceDialog extends DialogWrapper {
    private final Project project;
    private final WorkspaceService workspaceService;
    private JBList<WorkspaceInfo> workspaceList;
    private DefaultListModel<WorkspaceInfo> listModel;
    private WorkspaceInfo selectedWorkspace;

    public WorkspaceDialog(Project project) {
        super(project);
        this.project = project;
        ApiClient apiClient = PluginManager.getInstance().getApiClient();
        this.workspaceService = WorkspaceService.getInstance(apiClient);
        if (apiClient.testConnection()) {
            init();
            setTitle("Optimize SQL In ...");
        } else {
            ShowSettingsUtil.getInstance().showSettingsDialog(project, PawSettingPage.class);
            this.dispose();
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setPreferredSize(new Dimension(400, 300));

        // 创建工作空间列表
        listModel = new DefaultListModel<>();
        workspaceList = new JBList<>(listModel);
        workspaceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        workspaceList.setCellRenderer(new WorkspaceListCellRenderer());
        JBScrollPane scrollPane = new JBScrollPane(workspaceList);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // 创建刷新和创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshButton = new JButton("Refresh Workspaces");
        refreshButton.addActionListener(e -> refreshWorkspaceList());
        buttonPanel.add(refreshButton);

        JButton createButton = new JButton("Create a Workspace");
        createButton.addActionListener(e -> openCreateWorkspacePage());
        buttonPanel.add(createButton);
        contentPanel.add(buttonPanel, BorderLayout.NORTH);

        // 检查缓存中是否有工作空间列表
        List<WorkspaceInfo> cachedWorkspaces = workspaceService.getCachedWorkspaces();
        if (cachedWorkspaces != null && !cachedWorkspaces.isEmpty()) {
            // 使用缓存的工作空间列表
            listModel.clear();
            for (WorkspaceInfo workspace : cachedWorkspaces) {
                listModel.addElement(workspace);
            }
        } else {
            // 如果缓存为空，则刷新工作空间列表
            refreshWorkspaceList();
        }

        // 添加双击监听器
        workspaceList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    doOKAction();
                }
            }
        });

        return contentPanel;
    }

    private void refreshWorkspaceList() {
        try {
            List<WorkspaceInfo> workspaces = workspaceService.refreshWorkspaces();
            listModel.clear();
            for (WorkspaceInfo workspace : workspaces) {
                listModel.addElement(workspace);
            }
        } catch (Exception e) {
            close(CANCEL_EXIT_CODE);
        }
    }

    private void openCreateWorkspacePage() {
        try {
            PawSettingState settings = PawSettingState.getInstance();
            String frontendUrl = settings.getFrontendUrl();
            if (frontendUrl == null || frontendUrl.trim().isEmpty()) {
                frontendUrl = settings.getBaseUrl();
            }

            // 打开创建工作空间页面
            String createWorkspaceUrl = frontendUrl + "/app/workspaces/new-workspace";
            BrowserUtil.browse(createWorkspaceUrl);
        } catch (Exception e) {
            OptimizationNotifier.notifyError(project, "Failed to open create workspace page: " + e.getMessage());
            close(CANCEL_EXIT_CODE);
        }
    }

    @Override
    protected ValidationInfo doValidate() {
        if (workspaceList.getSelectedValue() == null) {
            return new ValidationInfo("Please choose a workspace ", workspaceList);
        }
        return null;
    }

    @Override
    protected void doOKAction() {
        selectedWorkspace = workspaceList.getSelectedValue();
        super.doOKAction();
    }

    public WorkspaceInfo getSelectedWorkspace() {
        return selectedWorkspace;
    }

    private static class WorkspaceListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus
        ) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof WorkspaceInfo) {
                WorkspaceInfo workspace = (WorkspaceInfo) value;
                setText(workspace.getWorkspaceName());
                setIcon(workspace.getIcon());
            }
            return this;
        }
    }
}
