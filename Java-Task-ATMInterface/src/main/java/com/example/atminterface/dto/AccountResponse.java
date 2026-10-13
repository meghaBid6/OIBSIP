
package com.example.atminterface.dto;

public class AccountResponse {

    private Long id;
    private String userId;
    private String name;
    private double balance;
    private String message;

    public AccountResponse() {
    }

    public AccountResponse(
            Long id,
            String userId,
            String name,
            double balance,
            String message) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.balance = balance;
        this.message = message;
    }

    public Long getId() {
        return id;
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

    public String getMessage() {
        return message;
    }
}
