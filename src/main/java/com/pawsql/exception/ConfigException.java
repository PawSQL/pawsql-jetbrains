package com.pawsql.exception;

public class ConfigException extends PawSqlException {
    private final String configKey;
    
    public ConfigException(String message, String configKey) {
        super(message);
        this.configKey = configKey;
    }
    
    public ConfigException(String message, Throwable cause, String configKey) {
        super(message, cause);
        this.configKey = configKey;
    }
    
    public String getConfigKey() {
        return configKey;
    }
}
