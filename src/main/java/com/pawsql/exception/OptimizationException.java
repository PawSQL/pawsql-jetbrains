package com.pawsql.exception;

public class OptimizationException extends PawSqlException {
    public OptimizationException(String message) {
        super(message);
    }
    
    public OptimizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
