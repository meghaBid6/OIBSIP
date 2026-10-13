package com.example.atminterface;

import com.example.atminterface.entity.Account;
import java.util.ArrayList;
import java.util.List;

public class Bank {

    private final String bankName = "OIBSIP ATM Bank";

    private final List<Account> accounts = new ArrayList<>();

    public void displayBankMessage() {
        System.out.println("Welcome to " + bankName);
    }

    public String getBankName() {
        return bankName;
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public Account findAccount(String userId) {
        for (Account account : accounts) {
            if (account.getUserId().equals(userId)) {
                return account;
            }
        }

        return null;
    }
}