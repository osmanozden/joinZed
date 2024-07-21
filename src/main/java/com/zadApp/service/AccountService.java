package com.zadApp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zadApp.model.Account;
import com.zadApp.repository.IAccountRepository;
import com.zadApp.util.ExchangeRateApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private IAccountRepository accountRepository;

    @Autowired
    private ExchangeRateApiClient exchangeRateApiClient;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${exchange.rate.api.url}")
    private String exchangeRateApiUrl;

    public void deposit(Long userId, String currency, Double amount) {
        Optional<Account> accountOpt = accountRepository.findByUserIdAndCurrency(userId, currency);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            account.setBalance(account.getBalance() + amount);
            accountRepository.save(account);
            kafkaTemplate.send("account-events", "Deposit successful for user " + userId);
        } else {
            kafkaTemplate.send("account-events", "Account not found for user " + userId);
        }
    }

    public void withdraw(Long userId, String currency, Double amount) {
        Optional<Account> accountOpt = accountRepository.findByUserIdAndCurrency(userId, currency);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            if (account.getBalance() >= amount) {
                account.setBalance(account.getBalance() - amount);
                accountRepository.save(account);
                kafkaTemplate.send("account-events", "Withdraw successful for user " + userId);
            } else {
                kafkaTemplate.send("account-events", "Insufficient funds for user " + userId);
            }
        } else {
            kafkaTemplate.send("account-events", "Account not found for user " + userId);
        }
    }

    public Double getBalance(Long userId, String currency) {
        Optional<Account> accountOpt = accountRepository.findByUserIdAndCurrency(userId, currency);
        return accountOpt.map(Account::getBalance).orElse(0.0);
    }

    public String exchange(Long userId, String fromCurrency, String toCurrency, Double amount) throws IOException {
        Optional<Account> fromAccountOpt = accountRepository.findByUserIdAndCurrency(userId, fromCurrency);
        Optional<Account> toAccountOpt = accountRepository.findByUserIdAndCurrency(userId, toCurrency);

        if (fromAccountOpt.isPresent() && toAccountOpt.isPresent()) {
            Account fromAccount = fromAccountOpt.get();
            Account toAccount = toAccountOpt.get();
            if (fromAccount.getBalance() >= amount) {
                double rate = exchangeRateApiClient.getExchangeRate(fromCurrency, toCurrency);
                fromAccount.setBalance(fromAccount.getBalance() - amount);
                toAccount.setBalance(toAccount.getBalance() + (amount * rate));
                accountRepository.save(fromAccount);
                accountRepository.save(toAccount);
                kafkaTemplate.send("account-events", "Exchange successful for user " + userId);
            } else {
                kafkaTemplate.send("account-events", "Insufficient funds for exchange for user " + userId);
            }
        } else {
            kafkaTemplate.send("account-events", "Account not found for user " + userId);
            return "Account not found for user " + userId;
        }
        return fromCurrency;
    }

    public List<Account> findAllAccounts() {
        return accountRepository.findAll();
    }

}

