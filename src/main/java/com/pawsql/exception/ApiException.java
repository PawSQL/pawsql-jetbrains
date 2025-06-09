package com.pawsql.exception;

public class ApiException extends PawSqlException {
    private final int statusCode;
    private final String errorCode;
    
    public ApiException(String message, int statusCode, String errorCode) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }
    
    public ApiException(String message, Throwable cause, int statusCode, String errorCode) {
        super(message, cause);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
