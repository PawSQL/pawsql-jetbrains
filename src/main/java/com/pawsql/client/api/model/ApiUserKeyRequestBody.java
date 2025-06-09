package com.pawsql.client.api.model;

import java.io.Serializable;

public class ApiUserKeyRequestBody implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userKey;

    public ApiUserKeyRequestBody(String userKey) {
        this.userKey = userKey;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }
}
