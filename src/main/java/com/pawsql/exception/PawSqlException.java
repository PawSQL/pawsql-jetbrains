package com.pawsql.exception;

public class PawSqlException extends RuntimeException {
    public PawSqlException(String message) {
        super(message);
    }
    
    public PawSqlException(String message, Throwable cause) {
        super(message, cause);
    }
}
