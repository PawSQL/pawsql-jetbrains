package com.pawsql.service;

import com.pawsql.exception.OptimizationException;
import com.pawsql.file.PawFileManager;
import com.pawsql.http.PawHttpClient;
import com.pawsql.model.StatementDetailInfoRead;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OptimizeResultHandler {
    private PawHttpClient httpClient;
    private final PawFileManager fileManager;
    
    public OptimizeResultHandler(PawHttpClient httpClient, PawFileManager fileManager) {
        this.httpClient = httpClient;
        this.fileManager = fileManager;
    }
    
    public StatementDetailInfoRead getOptimizationResult(String stmtId) throws IOException {
        return httpClient.get("/api/statement/detail/" + stmtId, StatementDetailInfoRead.class);
    }
    
    public void saveOptimizationResult(String sqlFilePath, StatementDetailInfoRead result) throws IOException {
        // 创建pawsql目录
        fileManager.createPawsqlDir(sqlFilePath);
        
        // 生成Markdown报告
        String markdownContent = fileManager.generateMarkdownReport(result);
        
        // 保存优化结果
        fileManager.saveOptimizationResult(
            sqlFilePath,
            result.getAnalysisName(),
            result.getStmtId(),
            markdownContent
        );
        
        // 清理旧的优化结果
        fileManager.cleanupOldResults(sqlFilePath, 10); // 保留最新的10个结果
    }
    
    public List<OptimizationResultInfo> listOptimizationResults(String sqlFilePath) throws IOException {
        Path[] resultFiles = fileManager.listOptimizationResults(sqlFilePath);
        
        return Arrays.stream(resultFiles)
            .map(path -> {
                try {
                    String content = fileManager.loadOptimizationResult(path.toString());
                    return parseResultInfo(path, content);
                } catch (IOException e) {
                    throw new OptimizationException("Failed to load optimization result: " + path, e);
                }
            })
            .collect(Collectors.toList());
    }
    
    public String loadOptimizationResult(String resultFilePath) throws IOException {
        return fileManager.loadOptimizationResult(resultFilePath);
    }
    
    public void deleteOptimizationResult(String resultFilePath) throws IOException {
        fileManager.deleteOptimizationResult(resultFilePath);
    }
    
    public void updateHttpClient(PawHttpClient httpClient) {
        this.httpClient = httpClient;
    }
    
    private OptimizationResultInfo parseResultInfo(Path path, String content) {
        OptimizationResultInfo info = new OptimizationResultInfo();
        info.setFilePath(path.toString());
        
        // 从文件名解析信息
        String fileName = path.getFileName().toString();
        String[] parts = fileName.split("_");
        if (parts.length >= 3) {
            info.setAnalysisName(parts[0]);
            info.setStmtId(parts[1]);
            info.setTimestamp(parts[2].replace(".md", ""));
        }
        
        // 从内容中提取基本信息
        String[] lines = content.split("\n");
        for (String line : lines) {
            if (line.startsWith("- **分析ID**:")) {
                info.setStmtId(line.substring(line.indexOf(":") + 1).trim());
            } else if (line.startsWith("- **分析名称**:")) {
                info.setAnalysisName(line.substring(line.indexOf(":") + 1).trim());
            } else if (line.startsWith("- **分析时间**:")) {
                info.setAnalysisTime(line.substring(line.indexOf(":") + 1).trim());
            } else if (line.startsWith("- **性能提升**:")) {
                String performanceStr = line.substring(line.indexOf(":") + 1).trim();
                performanceStr = performanceStr.replace("%", "");
                info.setPerformanceImprovement(Double.parseDouble(performanceStr));
            }
        }
        
        return info;
    }
    
    public static class OptimizationResultInfo {
        private String filePath;
        private String analysisName;
        private String stmtId;
        private String timestamp;
        private String analysisTime;
        private double performanceImprovement;
        
        // Getters and setters
        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }
        
        public String getAnalysisName() { return analysisName; }
        public void setAnalysisName(String analysisName) { this.analysisName = analysisName; }
        
        public String getStmtId() { return stmtId; }
        public void setStmtId(String stmtId) { this.stmtId = stmtId; }
        
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
        
        public String getAnalysisTime() { return analysisTime; }
        public void setAnalysisTime(String analysisTime) { this.analysisTime = analysisTime; }
        
        public double getPerformanceImprovement() { return performanceImprovement; }
        public void setPerformanceImprovement(double performanceImprovement) { 
            this.performanceImprovement = performanceImprovement; 
        }
    }
}
