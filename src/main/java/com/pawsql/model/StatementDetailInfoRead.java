package com.pawsql.model;

import lombok.Data;
import java.util.List;

@Data
public class StatementDetailInfoRead {
    private String analysisId;
    private String analysisName;
    private String stmtId;
    private String statementName;
    private String stmtText;
    private String detailMarkdown;
    private String detailMarkdownZh;
    private String openaiOptimizeTextEn;
    private String openaiOptimizeTextZh;
    private List<String> indexRecommended;
    private List<RuleRewrittenQuery> rewrittenQuery;
    private List<ViolationRule> violationRule;
    private ValidationDetails validationDetails;

    @Data
    public static class RuleRewrittenQuery {
        private String id;
        private String ruleCode;
        private String description;
        private String sql;
        private String rewrittenSql;
    }

    @Data
    public static class ViolationRule {
        private String ruleCode;
        private List<String> fragments;
    }

    @Data
    public static class ValidationDetails {
        private Double costBefore;
        private Double costAfter;
        private Double performance;
        private String originalPlan;
        private String optimizedPlan;
    }
}
