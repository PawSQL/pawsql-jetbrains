package com.pawsql.client;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.pawsql.client.api.ApiClient;
import com.pawsql.client.api.ApiResult;
import com.pawsql.notification.OptimizationNotifier;
import com.pawsql.service.WorkspaceService;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;
import java.util.ResourceBundle;

public class PawSettingPage implements Configurable {
    private JPanel main;
    private JCheckBox validate;
    private JCheckBox analyze;
    private JCheckBox rewrite;

    //index
    private JCheckBox dedupFlag;
    private JCheckBox indexOnly;
    private JTextField maxMembers4IndexOnly;
    private JTextField maxMembers;
    private JTextField maxPerTable;
    private JTextPane pawSQLAdvisorTextPane;
    private JButton testServerButton;
    private JComboBox lang;
    private JTextField server;
    private JTextField email;
    private JPasswordField password;
    private JLabel validateMessage;
    private JButton joinusButton;
    private JCheckBox updateStats;
    private JCheckBox communityEdition;

    private final Project project;
    private String cachedUserKey = null;
    private String cachedFrontendUrl = null;
    private final ResourceBundle bundle = ResourceBundle.getBundle("META-INF/locale");
    private final PawSettingState settingState = PawSettingState.getInstance();

    public PawSettingPage(Project proj) {
        project = proj;
        server.setText(settingState.getBaseUrl());
        email.setText(settingState.getEmail());
        password.setText(settingState.getPassword());
        cachedUserKey = settingState.getUserKey();
        cachedFrontendUrl = settingState.getFrontendUrl();
        lang.setSelectedItem(settingState.getLang());

        //database settings
        this.validate.setSelected(settingState.isValidate());
        this.analyze.setSelected(settingState.isAnalyze());
        this.rewrite.setSelected(settingState.isRewrite());
        this.updateStats.setSelected(settingState.isUpdateStats());
        //index advisor settings
        this.indexOnly.setSelected(settingState.isIndexOnly());
        this.dedupFlag.setSelected(settingState.isDedupIndex());
        this.maxMembers4IndexOnly.setText(String.valueOf(settingState.getMaxMembers4IndexOnly()));
        this.maxMembers.setText(String.valueOf(settingState.getMaxMembers()));
        this.maxPerTable.setText(String.valueOf(settingState.getMaxPerTable()));
        this.communityEdition.setSelected(settingState.isCommunityEdition());
        this.communityEdition.addActionListener(e -> {
            if (communityEdition.isSelected()) {
                this.email.setText("community@pawsql.com");
                this.email.setEditable(false);
                this.password.setText("community@pawsql.com");
                this.password.setEditable(false);
            } else {
                this.email.setText(settingState.getEmail());
                this.email.setEditable(true);
                this.password.setText(settingState.getPassword());
                this.password.setEditable(true);
            }
        });

        testServerButton.addActionListener(e -> testConnection(server.getText(), email.getText(), new String(password.getPassword())));
        joinusButton.addActionListener(e -> openSignupPage());
    }

    public @Nullable JComponent createComponent() {
        return main;
    }

    @Override
    public void apply() {
        settingState.setBaseUrl(server.getText());
        settingState.setEmail(email.getText());
        settingState.setPassword(new String(password.getPassword()));
        settingState.setDedupIndex(dedupFlag.isSelected());
        //execution
        settingState.setValidate(validate.isSelected());
        settingState.setAnalyze(analyze.isSelected());
        settingState.setRewrite(rewrite.isSelected());
        settingState.setUpdateStats(updateStats.isSelected());
        //index
        settingState.setDedupIndex(dedupFlag.isSelected());
        settingState.setIndexOnly(indexOnly.isSelected());
        settingState.setMaxMembers4IndexOnly(Integer.parseInt(maxMembers4IndexOnly.getText()));
        settingState.setMaxMembers(Integer.parseInt(maxMembers.getText()));
        settingState.setMaxPerTable(Integer.parseInt(maxPerTable.getText()));
        settingState.setLang(lang.getSelectedIndex());
        settingState.setCommunityEdition(communityEdition.isSelected());
        ApiClient apiClient = testConnection(server.getText(), email.getText(), new String(password.getPassword()));
        if (apiClient.getUserKey() != null) {
            PluginManager.getInstance().updateApiClient(apiClient);
            settingState.setUserKey(apiClient.getUserKey());
            settingState.setFrontendUrl(apiClient.getFrontUrl());
            WorkspaceService workspaceService = WorkspaceService.getInstance(apiClient);
            workspaceService.refreshWorkspaces();
        }
    }

    @Override
    public void reset() {
        this.server.setText(settingState.getBaseUrl());
        this.email.setText(settingState.getEmail());
        this.password.setText(settingState.getPassword());
        this.validate.setSelected(settingState.isValidate());
        this.analyze.setSelected(settingState.isAnalyze());
        this.rewrite.setSelected(settingState.isRewrite());
        this.updateStats.setSelected(settingState.isUpdateStats());
        //index advisor settings
        this.indexOnly.setSelected(settingState.isIndexOnly());
        this.dedupFlag.setSelected(settingState.isDedupIndex());
        this.maxMembers4IndexOnly.setText(String.valueOf(settingState.getMaxMembers4IndexOnly()));
        this.maxMembers.setText(String.valueOf(settingState.getMaxMembers()));
        this.maxPerTable.setText(String.valueOf(settingState.getMaxPerTable()));
        this.lang.setSelectedIndex(settingState.getLang());
        server.setText(settingState.getBaseUrl());
        email.setText(settingState.getEmail());
        password.setText(settingState.getPassword());
        validateMessage.setText("");
    }


    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) String getDisplayName() {
        return "A Configuration Page for PawSQL JetBrain";
    }

    private ApiClient testConnection(String url, String email, String pwd) {
        ApiClient apiClient = new ApiClient(url, email, pwd);
        try {
            ApiResult result = apiClient.validateUserKey();
            if (result.isSuccess()) {
                Map<String, String> data = (Map<String, String>) result.getData();
                String apiKey = data.get("apikey");
                String frontendUrlValue = data.get("frontendUrl");

                apiClient.setUserKey(apiKey);
                apiClient.setFrontUrl(frontendUrlValue);

                validateMessage.setText(bundle.getString("database.settings.validate.success"));
                validateMessage.setForeground(JBColor.GREEN);

                cachedUserKey = apiKey;
                cachedFrontendUrl = frontendUrlValue;
            } else {
                validateMessage.setText(bundle.getString("database.settings.validate.error"));
                validateMessage.setForeground(JBColor.RED);
            }
        } catch (Exception e) {
            validateMessage.setText(e.getMessage());
            validateMessage.setForeground(JBColor.RED);
        }
        return apiClient;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    private String getLanguageCode() {
        return "简体中文".equals(lang.getSelectedItem()) ? "zh" : "en";
    }

    private void openSignupPage() {
        try {
            PawSettingState settings = PawSettingState.getInstance();
            String frontendUrl = settings.getFrontendUrl();
            if (frontendUrl == null || frontendUrl.trim().isEmpty()) {
                frontendUrl = settings.getBaseUrl();
            }

            // 打开创建工作空间页面
            String createWorkspaceUrl = frontendUrl + "/signup";
            BrowserUtil.browse(createWorkspaceUrl);
        } catch (Exception e) {
            OptimizationNotifier.notifyError(project, "Failed to open signup page: " + e.getMessage());
        }
    }
}
