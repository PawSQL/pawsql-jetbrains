package com.pawsql.exception;

public enum ErrorCode {
    UNKNOWN_ERROR("unknown.error"),
    API_KEY_NOT_CONFIGURED("api.key.not.configured"),
    LICENSE_CODE_NOT_EXIST("license.code.not.exist"),
    LICENSE_CODE_NOT_VALID("license.code.not.valid"),
    BETA_ANALYSIS_OVER_LIMIT("beta.analysis.over.limit"),
    ANALYSIS_OVER_LIMIT("analysis.over.limit"),
    PLAN_USER_EXCEEDED_MAXIMUM_OPT_TIMES("plan.user.exceeded.maximum.opt.times"),
    WORKSPACE_NOT_EXIST("workspace.not.exist"),
    WORKSPACE_HAS_NO_ANALYSIS("workspace.has.no.analysis"),
    ANALYSIS_NOT_EXIST("analysis.not.exist"),
    CREATE_ANALYSIS_FAILED("error.create.analysis.failed"),
    LOGIN_INVALID_CREDENTIALS("error.login.invalidCredentials"),
    BACKEND_URL_INVALID("error.backendUrl.invalid"),
    FRONTEND_URL_INVALID("error.frontendUrl.invalid"),
    CONFIG_VALIDATE_FAILED("error.config.validate.failed"),
    LOAD_DATA_FAILED("error.load.data.failed"),
    NONEXISTENT("nonexistent"),
    UNACTIVE("unactive"),
    WORKLOAD_ONLY_SUPPORT_SINGLE_QUERY("workload.only.support.single.query");

    private final String messageKey;

    ErrorCode(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
