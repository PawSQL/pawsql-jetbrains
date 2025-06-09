package com.pawsql.exception;

import java.nio.file.Path;

public class FileOperationException extends PawSqlException {
    private final Path filePath;
    private final String operation;
    
    public FileOperationException(String message, Path filePath, String operation) {
        super(message);
        this.filePath = filePath;
        this.operation = operation;
    }
    
    public FileOperationException(String message, Throwable cause, Path filePath, String operation) {
        super(message, cause);
        this.filePath = filePath;
        this.operation = operation;
    }
    
    public Path getFilePath() {
        return filePath;
    }
    
    public String getOperation() {
        return operation;
    }
}
