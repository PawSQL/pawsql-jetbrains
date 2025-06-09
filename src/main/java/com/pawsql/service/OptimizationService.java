package com.pawsql.service;

import com.google.gson.Gson;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.pawsql.cache.WorkspaceCache;
import com.pawsql.client.PawSettingState;
import com.pawsql.client.PluginManager;
import com.pawsql.client.api.ApiClient;
import com.pawsql.client.api.ApiResult;
import com.pawsql.exception.ErrorCode;
import com.pawsql.exception.ErrorMessageResolver;
import com.pawsql.exception.LicenseException;
import com.pawsql.exception.OptimizationException;
import com.pawsql.file.PawFileManager;
import com.pawsql.http.PawHttpClient;
import com.pawsql.model.WorkspaceInfo;
import com.pawsql.notification.OptimizationNotifier;
import org.apache.log4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OptimizationService {
    private static final Logger logger = Logger.getLogger(OptimizationService.class);
    private static final int MAX_SQL_LENGTH = 10000; // SQL长度限制
    private final Gson gson = new Gson();

    private final Project project;
    private final ApiClient apiClient;
    private final OptimizeResultHandler resultHandler;
    private final WorkspaceCache workspaceCache;
    private final PawFileManager pawFileManager;
    private final WorkspaceService workspaceService;

    private static OptimizationService instance;

    private OptimizationService(Project project) {
        this.project = project;
        this.workspaceCache = WorkspaceCache.getInstance();
        this.pawFileManager = new PawFileManager();
        this.apiClient = PluginManager.getInstance().getApiClient();
        PawHttpClient httpClient = new PawHttpClient(
                apiClient.getBaseUrl(),
                apiClient.getUserKey()
        );
        this.workspaceService = WorkspaceService.getInstance(apiClient);
        this.resultHandler = new OptimizeResultHandler(httpClient, pawFileManager);
    }

    public static OptimizationService getInstance(Project project) {
        if (instance == null) {
            instance = new OptimizationService(project);
        }
        return instance;
    }

    public List<WorkspaceInfo> getWorkspaces() throws IOException {
        List<WorkspaceInfo> cachedWorkspaces = workspaceCache.getWorkspaces();
        if (!cachedWorkspaces.isEmpty() && !workspaceCache.isExpired()) {
            logger.debug("Using cached workspaces from OptimizationService");
            return cachedWorkspaces;
        }

        List<WorkspaceInfo> workspaces = workspaceService.getWorkspaces();
        workspaceCache.updateCache(workspaces);
        return workspaces;
    }

    public void optimizeSQL(String sqlFilePath, String sqlText, WorkspaceInfo workspace) {
//        Notification loadingNotification = null;
//        Notification processingNotification = null;
        try {
            logger.info("Starting SQL optimization for: ");
            logger.info(sqlText);
            // 验证SQL文本
            validateSQL(sqlText);

            // 创建优化请求
            Map<String, Object> request = new HashMap<>();
            PawSettingState settingState = PawSettingState.getInstance();
            request.put("workspaceId", workspace.getWorkspaceId());
            request.put("sqlText", sqlText);
            request.put("validateFlag", settingState.isValidate());  // 启用 What-If 分析
            request.put("analyzeFlag", settingState.isAnalyze());
            request.put("updateStatsBeforeValidationFlag", true);
            request.put("indexOnly", settingState.isIndexOnly());
            request.put("isRewrite", settingState.isRewrite());
            request.put("isDedupIndex", settingState.isDedupIndex());
            request.put("maxMembers", settingState.getMaxMembers());
            request.put("maxMembers4IndexOnly", settingState.getMaxMembers4IndexOnly());
            request.put("maxPerTable", settingState.getMaxPerTable());
            request.put("updateStatus", settingState.isUpdateStats());

            // 显示执行中的提示
//            loadingNotification = OptimizationNotifier.notifyInfo(project, "PawSQL Optimizing", "Sending optimization request to PawSQL Server...");

            // 发送优化请求
            logger.info("1. Sending optimization request to PawSQL Server...");
            ApiResult result = apiClient.optimizeSQL(request);
//            loadingNotification.expire();

            if (result.getCode() != 200 || result.getData() == null) {
                String errorCode = result.getMessage();
                String message;

                // 处理特定的错误码
                switch (errorCode) {
                    case "license.code.not.exist":
                        throw new LicenseException(ErrorMessageResolver.resolveErrorMessage(ErrorCode.LICENSE_CODE_NOT_EXIST));
                    case "license.code.not.valid":
                        throw new LicenseException(ErrorMessageResolver.resolveErrorMessage(ErrorCode.LICENSE_CODE_NOT_VALID));
                    case "beta.analysis.over.limit":
                        throw new OptimizationException(ErrorMessageResolver.resolveErrorMessage(ErrorCode.BETA_ANALYSIS_OVER_LIMIT));
                    case "analysis.over.limit":
                        throw new OptimizationException(ErrorMessageResolver.resolveErrorMessage(ErrorCode.ANALYSIS_OVER_LIMIT));
                    case "plan.user.exceeded.maximum.opt.times":
                        throw new OptimizationException(ErrorMessageResolver.resolveErrorMessage(ErrorCode.PLAN_USER_EXCEEDED_MAXIMUM_OPT_TIMES));
                    case "workspace.not.exist":
                        throw new OptimizationException(ErrorMessageResolver.resolveErrorMessage(ErrorCode.WORKSPACE_NOT_EXIST));
                    case "error.create.analysis.failed":
                        throw new OptimizationException(ErrorMessageResolver.resolveErrorMessage(ErrorCode.CREATE_ANALYSIS_FAILED));
                    default:
                        if (!errorCode.trim().isEmpty()) {
                            message = ErrorMessageResolver.resolveErrorMessage(errorCode);
                        } else {
                            message = "Please make sure all settings are correct.";
                        }
                        throw new OptimizationException("SQL Optimization failed: " + message);
                }
            }

            // 处理优化结果
//            processingNotification = OptimizationNotifier.notifyInfo(project, "PawSQL Optimizing", "Processing on PawSQL Server...");
            logger.debug("Processing optimization result");
            Map<String, Object> summaryData = (Map<String, Object>) result.getData();

            // 获取第一条语句的ID
            List<Map<String, Object>> statements = (List<Map<String, Object>>) summaryData.get("summaryStatementInfo");
            if (statements == null || statements.isEmpty()) {
                throw new OptimizationException("Can't find optimization details");
            }

            Map<String, Object> stmt = statements.get(0);
            String analysisStmtId = stmt.get("analysisStmtId").toString();
            logger.debug("Getting details for statement: " + analysisStmtId);

            // 获取语句详情
//            generatingNotification = OptimizationNotifier.notifyInfo(project, "PawSQL Optimizing", "Processing...");

            ApiResult detailResult = apiClient.getStatementDetails(analysisStmtId);
            if (detailResult.getCode() != 200 || detailResult.getData() == null) {
                String errorCode = detailResult.getMessage();
                String message;

                // 处理特定的错误码
                if (errorCode != null && !errorCode.trim().isEmpty()) {
                    message = ErrorMessageResolver.resolveErrorMessage(errorCode);
                } else {
                    message = ErrorMessageResolver.resolveErrorMessage(ErrorCode.UNKNOWN_ERROR);
                }
                throw new OptimizationException("Error when retrieving optimization result: " + message);
            }

            // 根据语言选择内容
            int language = PawSettingState.getInstance().getLang();
            String markdownKey = language == 1 ? "detailMarkdownZh" : "detailMarkdown";

            Map<String, Object> detailData = (Map<String, Object>) detailResult.getData();
            String markdown = detailData.get(markdownKey).toString();
            String analysisName = summaryData.get("analysisName").toString();
            String url = settingState.getFrontendUrl() + "/statement/" + analysisStmtId;

            if (language == 1)
                markdown = markdown + "\n\n" + "- 在 " + apiClient.getFrontUrl() + " 上<a href=" + url + ">查看更多优化详情.</a> \n";
            else
                markdown = markdown + "\n\n" + "- <a href=" + url + ">More Optimization Details</a> on " + apiClient.getFrontUrl() + ".\n";

            // 写入优化结果
//            logger.info("Creating PawSQL directory");
            pawFileManager.createPawsqlDir(sqlFilePath);
            logger.debug("5. Saving optimization result for " + sqlFilePath);
            Path resultPath = pawFileManager.saveOptimizationResult(sqlFilePath, analysisName, "summary", markdown);

            if (!EventQueue.isDispatchThread()) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    // 打开结果文件
                    VirtualFile resultFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(resultPath.toString());
                    if (resultFile != null) {
                        FileEditorManager.getInstance(project).openFile(resultFile, true);
                    }
                    // 发送成功通知
                    OptimizationNotifier.notifyInfo(project, "Optimization Completed!", "<a href=" + url + ">More Details on PawSQL Server.</a>");
                });
            }


            logger.info("Optimization completed, Check Details at " + url);

//            processingNotification.expire();

        } catch (Exception e) {
            logger.error("SQL optimization failed", e);
            // 确保关闭所有通知
//            if (loadingNotification != null) loadingNotification.expire();
//            if (processingNotification != null) processingNotification.expire();
            if (!EventQueue.isDispatchThread()) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    OptimizationNotifier.notifyError(project, e.getMessage());
                });
            }

        }
    }

    private void validateSQL(String sqlText) {
        if (sqlText == null || sqlText.trim().isEmpty()) {
            throw new OptimizationException("SQL文本不能为空");
        }

        if (sqlText.length() > MAX_SQL_LENGTH) {
            throw new OptimizationException(
                    String.format("SQL文本长度(%d)超过最大限制(%d)", sqlText.length(), MAX_SQL_LENGTH)
            );
        }
    }
}
