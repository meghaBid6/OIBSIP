
package com.example.atminterface;

import com.example.atminterface.entity.Transaction;
import com.example.atminterface.entity.Account;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ATM {

    private final List<Transaction> transactions = new ArrayList<>();

    private final Bank bank = new Bank();

    private final Account account =
            new Account("ATM1001", "ATM User", "1234", 1000.0);

    private final Account recipientAccount =
            new Account("ATM1002", "Recipient User", "5678", 500.0);

    public ATM() {
        bank.addAccount(account);
        bank.addAccount(recipientAccount);
    }


    public boolean login(Scanner scanner) {
        String correctUserId = account.getUserId();
        String correctPin = account.getPin();

        int attempts = 0;

        while (attempts < 3) {
            System.out.print("Enter User ID: ");
            String userId = scanner.next();

            System.out.print("Enter PIN: ");
            String pin = scanner.next();

            if (userId.equals(correctUserId) && pin.equals(correctPin)) {
                System.out.println("Login successful");
                System.out.println("Welcome, " + account.getName() + "!");
                return true;
            }

            attempts++;
            System.out.println("Incorrect User ID or PIN");

            if (attempts < 3) {
                System.out.println("Attempts remaining: " + (3 - attempts));
            }
        }

        System.out.println("Access denied. Too many incorrect attempts.");
        return false;
    }

    public void start(Scanner scanner) {
        int choice = 0;

        while (choice != 6) {
            System.out.println("\n===== ATM MENU =====");
            System.out.println("1. Transaction History");
            System.out.println("2. Withdraw");
            System.out.println("3. Deposit");
            System.out.println("4. Transfer");
            System.out.println("5. Check Balance");
            System.out.println("6. Quit");
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number from 1 to 6.");
                scanner.next();
                continue;
            }

            choice = scanner.nextInt();

            switch (choice) {

                case 1:
                    if (transactions.isEmpty()) {
                        System.out.println("No transactions found");
                    } else {
                        System.out.println("\n===== TRANSACTION HISTORY =====");

                        for (Transaction transaction : transactions) {
                            System.out.print(
                                    "User ID: " + transaction.getUserId()
                                            + " | Type: " + transaction.getType()
                                            + " | Amount: " + transaction.getAmount()
                            );

                            if (transaction.getRecipientUserId() != null) {
                                System.out.print(
                                        " | Recipient: " + transaction.getRecipientUserId()
                                );
                            }

                            System.out.println(
                                    " | Time: " + transaction.getTransactionTime()
                            );
                        }
                    }
                    break;

                case 2:
                    withdraw(scanner);
                    break;
                case 3:
                    deposit(scanner);
                    break;
                case 4:
                    transfer(scanner);
                    break;
                case 5:
                    checkBalance();
                    break;

                case 6:
                    System.out.println("Thank you for using ATM");
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    public void deposit(Scanner scanner) {
        System.out.print("Enter deposit amount: ");
        if (!scanner.hasNextDouble()) {
            System.out.println("Invalid amount. Please enter a number.");
            scanner.next();
            return;
        }

        double amount = scanner.nextDouble();

        if (amount <= 0) {
            System.out.println("Deposit amount must be greater than zero");
            return;
        }

        account.setBalance(account.getBalance() + amount);

        Transaction transaction = new Transaction();
        transaction.setType("DEPOSIT");
        transaction.setAmount(amount);
        transaction.setTransactionTime(java.time.LocalDateTime.now());
        transaction.setUserId(account.getUserId());

        transactions.add(transaction);

        System.out.println("Deposit successful");
        System.out.println("Updated balance: " + account.getBalance());
    }
    public void withdraw(Scanner scanner) {
        System.out.print("Enter withdrawal amount: ");

        if (!scanner.hasNextDouble()) {
            System.out.println("Invalid amount. Please enter a number.");
            scanner.next();
            return;
        }

        double amount = scanner.nextDouble();

        if (amount <= 0) {
            System.out.println("Withdrawal amount must be greater than zero");
            return;
        }


        if (amount > account.getBalance()) {
            System.out.println("Insufficient Funds");
            return;
        }

        account.setBalance(account.getBalance() - amount);
        Transaction transaction = new Transaction();
        transaction.setType("WITHDRAW");
        transaction.setUserId(account.getUserId());
        transaction.setAmount(amount);
        transaction.setTransactionTime(java.time.LocalDateTime.now());

        transactions.add(transaction);

        System.out.println("Withdrawal successful");
        System.out.println("Updated balance: " + account.getBalance());
    }

    public void transfer(Scanner scanner) {

        System.out.print("Enter recipient User ID: ");
        String recipientUserId = scanner.next();

        if (recipientUserId.equals(account.getUserId())) {
            System.out.println("Cannot transfer money to your own account");
            return;
        }

        Account recipient = bank.findAccount(recipientUserId);

        if (recipient == null) {
            System.out.println("Recipient account not found");
            return;
        }

        System.out.print("Enter transfer amount: ");

        if (!scanner.hasNextDouble()) {
            System.out.println("Invalid amount. Please enter a number.");
            scanner.next();
            return;
        }

        double amount = scanner.nextDouble();

        if (amount <= 0) {
            System.out.println("Transfer amount must be greater than zero");
            return;
        }

        if (amount > account.getBalance()) {
            System.out.println("Insufficient Funds");
            return;
        }

        // Deduct money from the sender
        account.setBalance(account.getBalance() - amount);

        // Add money to the recipient
        recipient.setBalance(recipient.getBalance() + amount);

        // Record the sender's transaction
        Transaction transaction = new Transaction();
        transaction.setType("TRANSFER");
        transaction.setUserId(account.getUserId());
        transaction.setAmount(amount);
        transaction.setRecipientUserId(recipientUserId);
        transaction.setTransactionTime(java.time.LocalDateTime.now());

        transactions.add(transaction);

        Transaction receivedTransaction = new Transaction();
        receivedTransaction.setType("TRANSFER_RECEIVED");
        receivedTransaction.setUserId(recipient.getUserId());
        receivedTransaction.setAmount(amount);
        receivedTransaction.setRecipientUserId(account.getUserId());
        receivedTransaction.setTransactionTime(java.time.LocalDateTime.now());

        transactions.add(receivedTransaction);

        System.out.println("Transfer successful");
        System.out.println("Recipient: " + recipientUserId);
        System.out.println("Transferred amount: " + amount);
        System.out.println("Your updated balance: " + account.getBalance());
        System.out.println("Recipient updated balance: " + recipient.getBalance());
    }

    public void checkBalance() {
        System.out.println("Current balance: " + account.getBalance());
    }



}

