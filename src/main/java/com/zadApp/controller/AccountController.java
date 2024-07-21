package com.zadApp.controller;

import com.zadApp.model.Account;
import com.zadApp.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/accounts")
@Tag(name = "Account Management", description = "Endpoints for managing user accounts")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@RequestParam Long userId,
                                          @RequestParam String currency,
                                          @RequestParam Double amount) {
        accountService.deposit(userId, currency, amount);
        return ResponseEntity.ok("Deposit request received");
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@RequestParam Long userId,
                                           @RequestParam String currency,
                                           @RequestParam Double amount) {
        accountService.withdraw(userId, currency, amount);
        return ResponseEntity.ok("Withdraw request received");
    }

    @GetMapping("/balance")
    public ResponseEntity<Double> getBalance(@RequestParam Long userId,
                                             @RequestParam String currency) {
        Double balance = accountService.getBalance(userId, currency);
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/exchange")
    public ResponseEntity<String> exchange(@RequestParam Long userId,
                                           @RequestParam String fromCurrency,
                                           @RequestParam String toCurrency,
                                           @RequestParam Double amount) {
        try {
            accountService.exchange(userId, fromCurrency, toCurrency, amount);
            return ResponseEntity.ok("Exchange request received");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to retrieve exchange rate");
        }
    }

    @GetMapping("/getAllAccounts")
    @Operation(summary = "Get all accounts")
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.findAllAccounts();
        return ResponseEntity.ok(accounts);
    }
}
