
package com.example.atminterface.dto;

public class LoginResponse {

    private String message;
    private String userId;
    private String name;
    private double balance;

    public LoginResponse() {
    }

    public LoginResponse(
            String message,
            String userId,
            String name,
            double balance) {
        this.message = message;
        this.userId = userId;
        this.name = name;
        this.balance = balance;
    }

    public String getMessage() {
        return message;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }
}