
package com.example.atminterface.service;

import com.example.atminterface.dto.ChangePinRequest;
import com.example.atminterface.dto.LoginResponse;
import com.example.atminterface.dto.TransferRequest;
import com.example.atminterface.entity.Account;
import com.example.atminterface.entity.Transaction;
import com.example.atminterface.repository.AccountRepository;
import com.example.atminterface.repository.TransactionRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ATMService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    public ATMService(
            AccountRepository accountRepository,
            TransactionRepository transactionRepository,
            PasswordEncoder passwordEncoder) {

        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 1. Create a new account
    public Account createAccount(
            String userId,
            String name,
            String pin,
            double balance) {

        if (userId == null || userId.isBlank()) {
            throw new RuntimeException("User ID is required");
        }

        if (accountRepository.findByUserId(userId).isPresent()) {
            throw new RuntimeException("User ID already exists");
        }

        if (name == null || name.isBlank()) {
            throw new RuntimeException(
                    "Account holder name is required");
        }

        if (pin == null || !pin.matches("\\d{4}")) {
            throw new RuntimeException(
                    "PIN must contain exactly 4 digits");
        }

        if (balance < 0) {
            throw new RuntimeException(
                    "Balance cannot be negative");
        }

        // Keep existing PIN storage compatible for now.
        String encodedPin = passwordEncoder.encode(pin);

        Account account = new Account(userId, name, encodedPin, balance);

        return accountRepository.save(account);
    }

    // 2. Login with PIN and lock after 3 incorrect attempts
    public LoginResponse login(String userId, String pin) {

        if (userId == null || userId.isBlank()) {
            throw new RuntimeException("User ID is required");
        }

        if (pin == null || pin.isBlank()) {
            throw new RuntimeException("PIN is required");
        }

        if (!pin.matches("\\d{4}")) {
            throw new RuntimeException(
                    "PIN must contain exactly 4 digits");
        }

        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("User ID not found"));

        if (account.isLocked()) {
            throw new RuntimeException(
                    "Account is locked after 3 incorrect PIN attempts");
        }

        // Existing plain-text PIN comparison.
        String storedPin = account.getPin();

        boolean pinMatches;

        if (storedPin.startsWith("$2a$")
                || storedPin.startsWith("$2b$")
                || storedPin.startsWith("$2y$")) {

            pinMatches = passwordEncoder.matches(pin, storedPin);

        } else {
            // Supports accounts created before BCrypt was added.
            pinMatches = storedPin.equals(pin);

            if (pinMatches) {
                // Upgrade the old PIN to a BCrypt hash after successful login.
                account.setPin(passwordEncoder.encode(pin));
            }
        }

        if (!pinMatches) {

            int attempts = account.getFailedAttempts() + 1;
            account.setFailedAttempts(attempts);

            if (attempts >= 3) {
                account.setLocked(true);
                accountRepository.save(account);

                throw new RuntimeException(
                        "Account locked after 3 incorrect PIN attempts");
            }

            accountRepository.save(account);

            throw new RuntimeException(
                    "Incorrect PIN. Attempts remaining: "
                            + (3 - attempts));
        }

        account.setFailedAttempts(0);
        accountRepository.save(account);

        return new LoginResponse(
                "Login successful",
                account.getUserId(),
                account.getName(),
                account.getBalance()
        );
    }

    // 3. Check account balance
    public double checkBalance(String userId) {

        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("User ID not found"));

        return account.getBalance();
    }

    // 4. Deposit money
    @Transactional
    public double deposit(String userId, double amount) {

        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("User ID not found"));

        if (!Double.isFinite(amount) || amount <= 0) {
            throw new RuntimeException(
                    "Deposit amount must be greater than zero");
        }

        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setType("DEPOSIT");
        transaction.setAmount(amount);
        transaction.setUserId(userId);
        transaction.setTransactionTime(LocalDateTime.now());

        transactionRepository.save(transaction);

        return account.getBalance();
    }

    // 5. Withdraw money
    @Transactional
    public double withdraw(String userId, double amount) {

        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("User ID not found"));

        if (!Double.isFinite(amount) || amount <= 0) {
            throw new RuntimeException(
                    "Withdrawal amount must be greater than zero");
        }

        if (account.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setType("WITHDRAW");
        transaction.setAmount(amount);
        transaction.setUserId(userId);
        transaction.setTransactionTime(LocalDateTime.now());

        transactionRepository.save(transaction);

        return account.getBalance();
    }

    // 6. View transaction history
    public List<Transaction> getTransactionHistory(String userId) {

        accountRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("User ID not found"));

        return transactionRepository
                .findByUserIdOrderByTransactionTimeDesc(userId);
    }

    // 7. Transfer money between two accounts
    @Transactional
    public String transfer(TransferRequest request) {

        if (request == null
                || request.getSenderUserId() == null
                || request.getRecipientUserId() == null
                || request.getSenderUserId().isBlank()
                || request.getRecipientUserId().isBlank()) {

            throw new RuntimeException(
                    "Sender and recipient user IDs are required");
        }

        if (!Double.isFinite(request.getAmount()) || request.getAmount() <= 0) {
            throw new RuntimeException(
                    "Transfer amount must be greater than zero");
        }

        if (request.getSenderUserId().equals(
                request.getRecipientUserId())) {

            throw new RuntimeException(
                    "Sender and recipient cannot be the same");
        }

        Account sender = accountRepository
                .findByUserId(request.getSenderUserId())
                .orElseThrow(() ->
                        new RuntimeException("Sender account not found"));

        Account recipient = accountRepository
                .findByUserId(request.getRecipientUserId())
                .orElseThrow(() ->
                        new RuntimeException("Recipient account not found"));

        if (sender.getBalance() < request.getAmount()) {
            throw new RuntimeException("Insufficient balance");
        }

        sender.setBalance(
                sender.getBalance() - request.getAmount());

        recipient.setBalance(
                recipient.getBalance() + request.getAmount());

        accountRepository.save(sender);
        accountRepository.save(recipient);

        Transaction sentTransaction = new Transaction();
        sentTransaction.setType("TRANSFER_SENT");
        sentTransaction.setAmount(request.getAmount());
        sentTransaction.setUserId(sender.getUserId());
        sentTransaction.setRecipientUserId(recipient.getUserId());
        sentTransaction.setTransactionTime(LocalDateTime.now());

        transactionRepository.save(sentTransaction);

        Transaction receivedTransaction = new Transaction();
        receivedTransaction.setType("TRANSFER_RECEIVED");
        receivedTransaction.setAmount(request.getAmount());
        receivedTransaction.setUserId(recipient.getUserId());
        receivedTransaction.setRecipientUserId(sender.getUserId());
        receivedTransaction.setTransactionTime(LocalDateTime.now());

        transactionRepository.save(receivedTransaction);

        return "Transfer successful";
    }

    // 8. Change account PIN
    @Transactional
    public String changePin(ChangePinRequest request) {

        if (request == null
                || request.getUserId() == null
                || request.getUserId().isBlank()) {

            throw new RuntimeException("User ID is required");
        }

        if (request.getCurrentPin() == null
                || request.getCurrentPin().isBlank()) {

            throw new RuntimeException("Current PIN is required");
        }

        if (request.getNewPin() == null
                || request.getNewPin().isBlank()) {

            throw new RuntimeException("New PIN is required");
        }

        Account account = accountRepository
                .findByUserId(request.getUserId())
                .orElseThrow(() ->
                        new RuntimeException("User ID not found"));

        if (account.isLocked()) {
            throw new RuntimeException(
                    "Account is locked. PIN cannot be changed");
        }

        String storedPin = account.getPin();

        boolean currentPinMatches;

        if (storedPin.startsWith("$2a$")
                || storedPin.startsWith("$2b$")
                || storedPin.startsWith("$2y$")) {

            currentPinMatches = passwordEncoder.matches(
                    request.getCurrentPin(), storedPin);

        } else {
            // Supports accounts that still have a plain-text PIN.
            currentPinMatches = storedPin.equals(request.getCurrentPin());
        }

        if (!currentPinMatches) {
            throw new RuntimeException("Current PIN is incorrect");
        }

        if (!request.getNewPin().matches("\\d{4}")) {
            throw new RuntimeException(
                    "New PIN must contain exactly 4 digits");
        }

        if (request.getCurrentPin().equals(request.getNewPin())) {
            throw new RuntimeException(
                    "New PIN must be different from current PIN");
        }

        // Keep existing PIN storage compatible for now.
        String encodedNewPin =
                passwordEncoder.encode(request.getNewPin());

        account.setPin(encodedNewPin);
        accountRepository.save(account);

        return "PIN changed successfully";
    }
}
