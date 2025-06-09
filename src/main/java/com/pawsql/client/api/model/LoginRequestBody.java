package com.pawsql.client.api.model;

import java.io.Serializable;

public class LoginRequestBody implements Serializable {
    private static final long serialVersionUID = 1L;

    private String email;
    private String password;

    public LoginRequestBody(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
