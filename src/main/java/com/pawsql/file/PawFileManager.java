package com.pawsql.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawsql.exception.OptimizationException;
import com.pawsql.model.StatementDetailInfoRead;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.stream.Stream;

public class PawFileManager {
    private static final Logger logger = Logger.getLogger(PawFileManager.class);
    private static final String PAWSQL_DIR = "PawSQL";
    private static final String RESULT_FILE_EXTENSION = ".md";
    private static final java.nio.charset.Charset CHARSET = java.nio.charset.StandardCharsets.UTF_8;

    public PawFileManager() {
        ObjectMapper objectMapper = new ObjectMapper();
    }

    public void createPawsqlDir(String sqlFilePath) throws IOException {
        if (sqlFilePath == null || sqlFilePath.trim().isEmpty()) {
            throw new IllegalArgumentException("SQL文件路径不能为空");
        }
        logger.debug("Creating PawSQL directory for: " + sqlFilePath);
        Path sqlPath = Paths.get(sqlFilePath);
        if (!Files.exists(sqlPath.getParent())) {
            throw new IllegalArgumentException("SQL文件所在目录不存在: " + sqlPath.getParent());
        }
        Path pawsqlDir = sqlPath.getParent().resolve(PAWSQL_DIR);

        if (!Files.exists(pawsqlDir)) {
            logger.debug("Creating directory: " + pawsqlDir);
            Files.createDirectories(pawsqlDir);
        }
        logger.debug("PawSQL directory created successfully");
    }

    public Path saveOptimizationResult(String sqlFilePath, String analysisName, String stmtId, String markdownContent) throws IOException {
        if (sqlFilePath == null || sqlFilePath.trim().isEmpty()) {
            throw new IllegalArgumentException("SQL文件路径不能为空");
        }
        if (analysisName == null || analysisName.trim().isEmpty()) {
            throw new IllegalArgumentException("分析名称不能为空");
        }
        if (stmtId == null || stmtId.trim().isEmpty()) {
            throw new IllegalArgumentException("语句ID不能为空");
        }
        if (markdownContent == null) {
            throw new IllegalArgumentException("优化结果内容不能为空");
        }
        logger.debug("Saving optimization result for: " + sqlFilePath);
        Path sqlPath = Paths.get(sqlFilePath);
        Path pawsqlDir = sqlPath.getParent().resolve(PAWSQL_DIR);

        // 只使用分析名称作为文件名
        String fileName = sanitizeFileName(analysisName) + RESULT_FILE_EXTENSION;

        Path resultFile = pawsqlDir.resolve(fileName);
        logger.debug("Writing to file: " + resultFile);
        try (BufferedWriter writer = Files.newBufferedWriter(resultFile, CHARSET)) {
            writer.write(markdownContent);
            writer.flush();
        }
        logger.debug("File written successfully");
        return resultFile;
    }

    public String loadOptimizationResult(String resultFilePath) throws IOException {
        logger.info("Loading optimization result from: " + resultFilePath);
        Path path = Paths.get(resultFilePath);
        if (!Files.exists(path)) {
            throw new OptimizationException("优化结果文件不存在: " + resultFilePath);
        }
        return new String(Files.readAllBytes(path), CHARSET);
    }

    public Path[] listOptimizationResults(String sqlFilePath) throws IOException {
        logger.info("Listing optimization results for: " + sqlFilePath);
        Path sqlPath = Paths.get(sqlFilePath);
        Path pawsqlDir = sqlPath.getParent().resolve(PAWSQL_DIR);

        if (!Files.exists(pawsqlDir)) {
            return new Path[0];
        }

        try (Stream<Path> stream = Files.list(pawsqlDir)) {
            return stream
                    .filter(path -> path.toString().endsWith(RESULT_FILE_EXTENSION))
                    .sorted((p1, p2) -> {
                        try {
                            return -Files.getLastModifiedTime(p1)
                                    .compareTo(Files.getLastModifiedTime(p2));
                        } catch (IOException e) {
                            return 0;
                        }
                    })
                    .toArray(Path[]::new);
        }
    }

    public void deleteOptimizationResult(String resultFilePath) throws IOException {
        logger.info("Deleting optimization result: " + resultFilePath);
        Path path = Paths.get(resultFilePath);
        if (!Files.exists(path)) {
            throw new OptimizationException("优化结果文件不存在: " + resultFilePath);
        }

        Files.delete(path);
        logger.info("File deleted successfully");
    }

    public void cleanupOldResults(String sqlFilePath, int maxHistoryFiles) throws IOException {
        logger.info("Cleaning up old results for: " + sqlFilePath);
        Path[] results = listOptimizationResults(sqlFilePath);

        if (results.length > maxHistoryFiles) {
            // 删除超出限制的最旧的文件
            for (int i = maxHistoryFiles; i < results.length; i++) {
                logger.info("Deleting old result: " + results[i]);
                Files.delete(results[i]);
            }
        }
        logger.info("Old results cleaned up successfully");
    }

    private String sanitizeFileName(String name) {
        return name.replaceAll("[^a-zA-Z0-9.-]", "_");
    }

    public String generateMarkdownReport(StatementDetailInfoRead result) {
        logger.info("Generating markdown report for: " + result.getStmtId());
        StringBuilder markdown = new StringBuilder();

        // 添加标题
        markdown.append("# SQL优化报告\n\n");

        // 添加基本信息
        markdown.append("## 基本信息\n\n");
        markdown.append("- **分析ID**: ").append(result.getStmtId()).append("\n");
        markdown.append("- **分析名称**: ").append(result.getAnalysisName()).append("\n");
        markdown.append("- **分析时间**: ").append(LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n\n");

        // 添加原始SQL
        markdown.append("## 原始SQL\n\n```sql\n").append(result.getStmtText()).append("\n```\n\n");

        // 添加优化建议
        if (result.getRewrittenQuery() != null && !result.getRewrittenQuery().isEmpty()) {
            markdown.append("## 优化建议\n\n");
            result.getRewrittenQuery().forEach(rewrite -> {
                markdown.append("### 建议").append(rewrite.getId()).append("\n\n");
                markdown.append(rewrite.getDescription()).append("\n\n");
                markdown.append("```sql\n").append(rewrite.getSql()).append("\n```\n\n");
            });
        }

        // 添加索引建议
        if (result.getIndexRecommended() != null && !result.getIndexRecommended().isEmpty()) {
            markdown.append("## 索引建议\n\n");
            result.getIndexRecommended().forEach(index -> {
                markdown.append("- ").append(index).append("\n");
            });
            markdown.append("\n");
        }

        // 添加性能分析
        if (result.getValidationDetails() != null) {
            markdown.append("## 性能分析\n\n");
            markdown.append("- **性能提升**: ").append(String.format("%.2f%%", result.getValidationDetails().getPerformance())).append("\n");
            markdown.append("- **执行计划对比**:\n\n");
            markdown.append("### 原始执行计划\n```\n").append(result.getValidationDetails().getOriginalPlan()).append("\n```\n\n");
            markdown.append("### 优化后执行计划\n```\n").append(result.getValidationDetails().getOptimizedPlan()).append("\n```\n");
        }

        return markdown.toString();
    }
}
