
package com.example.atminterface.dto;

public class LoginRequest {

    private String userId;
    private String pin;

    public LoginRequest() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}

