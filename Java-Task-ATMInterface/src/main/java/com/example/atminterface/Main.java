package com.example.atminterface;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Bank bank = new Bank();
        bank.displayBankMessage();

        ATM atm = new ATM();
        Scanner scanner = new Scanner(System.in);

        boolean loggedIn = atm.login(scanner);

        if (loggedIn) {
            atm.start(scanner);
        } else {
            System.out.println("ATM session ended.");
        }

        scanner.close();
    }
}