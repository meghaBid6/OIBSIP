
package com.example.atminterface.controller;

import com.example.atminterface.dto.LoginRequest;
import com.example.atminterface.dto.LoginResponse;
import com.example.atminterface.entity.Account;
import com.example.atminterface.service.ATMService;
import org.springframework.web.bind.annotation.*;
import com.example.atminterface.entity.Transaction;
import java.util.List;
import com.example.atminterface.dto.TransferRequest;
import com.example.atminterface.dto.ChangePinRequest;
import com.example.atminterface.dto.AccountResponse;

@RestController
@RequestMapping("/api/atm")
public class ATMController {

    private final ATMService atmService;

    public ATMController(ATMService atmService) {
        this.atmService = atmService;
    }


    @PostMapping("/accounts")
    public AccountResponse createAccount(@RequestBody Account account) {

        Account savedAccount = atmService.createAccount(
                account.getUserId(),
                account.getName(),
                account.getPin(),
                account.getBalance()
        );

        return new AccountResponse(
                savedAccount.getId(),
                savedAccount.getUserId(),
                savedAccount.getName(),
                savedAccount.getBalance(),
                "Account created successfully"
        );
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return atmService.login(
                request.getUserId(),
                request.getPin()
        );
    }

    @GetMapping("/balance/{userId}")
    public double checkBalance(@PathVariable String userId) {
        return atmService.checkBalance(userId);
    }

    @PostMapping("/deposit/{userId}")
    public double deposit(
            @PathVariable String userId,
            @RequestParam double amount) {

        return atmService.deposit(userId, amount);
    }

    @PostMapping("/withdraw/{userId}")
    public double withdraw(
            @PathVariable String userId,
            @RequestParam double amount) {

        return atmService.withdraw(userId, amount);
    }

    @GetMapping("/transactions/{userId}")
    public List<Transaction> getTransactionHistory(
            @PathVariable String userId) {
        return atmService.getTransactionHistory(userId);
    }

    @PostMapping("/transfer")
    public String transfer(@RequestBody TransferRequest request) {
        return atmService.transfer(request);
    }

    @PostMapping("/change-pin")
    public String changePin(@RequestBody ChangePinRequest request) {
        return atmService.changePin(request);
    }

}