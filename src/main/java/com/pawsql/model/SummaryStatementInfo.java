package com.pawsql.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SummaryStatementInfo {
    private String analysisStmtId;
    private String stmtId;
    private String stmtName;
    private String stmtType;
    private String stmtText;
    private Double costBefore;
    private Double costAfter;
    private Integer numberOfRewrite;
    private Integer numberOfRewriteRules;
    private Integer numberOfViolations;
    private Integer numberOfSyntaxError;
    private Integer numberOfIndex;
    private Integer numberOfHitIndex;
    private Double performance;
    private String contributingIndices;
}
