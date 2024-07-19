package com.joinzad.interviewcase.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joinzad.interviewcase.model.AccountModel;
import com.joinzad.interviewcase.repository.IAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private IAccountRepository accountRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${exchange.rate.api.url}")
    private String exchangeRateApiUrl;

    public void deposit(Long userId, String currency, Double amount) {
        Optional<AccountModel> accountOpt = accountRepository.findByUserIdAndCurrency(userId, currency);
        if (accountOpt.isPresent()) {
            AccountModel accountModel = accountOpt.get();
            accountModel.setBalance(accountModel.getBalance() + amount);
            accountRepository.save(accountModel);
            kafkaTemplate.send("account-events", "Deposit successful for user " + userId);
        } else {
            kafkaTemplate.send("account-events", "Account not found for user " + userId);
        }
    }

    public void withdraw(Long userId, String currency, Double amount) {
        Optional<AccountModel> accountOpt = accountRepository.findByUserIdAndCurrency(userId, currency);
        if (accountOpt.isPresent()) {
            AccountModel account = accountOpt.get();
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
        Optional<AccountModel> accountOpt = accountRepository.findByUserIdAndCurrency(userId, currency);
        return accountOpt.map(AccountModel::getBalance).orElse(0.0);
    }

    public void exchange(Long userId, String fromCurrency, String toCurrency, Double amount) throws IOException {
        Optional<AccountModel> fromAccountOpt = accountRepository.findByUserIdAndCurrency(userId, fromCurrency);
        Optional<AccountModel> toAccountOpt = accountRepository.findByUserIdAndCurrency(userId, toCurrency);

        if (fromAccountOpt.isPresent() && toAccountOpt.isPresent()) {
            AccountModel fromAccount = fromAccountOpt.get();
            AccountModel toAccount = toAccountOpt.get();
            if (fromAccount.getBalance() >= amount) {
                double rate = getExchangeRate(fromCurrency, toCurrency);
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
        }
    }

    private double getExchangeRate(String fromCurrency, String toCurrency) throws IOException {
        String url = String.format("%s/latest/%s", exchangeRateApiUrl, fromCurrency);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode root = new ObjectMapper().readTree(response.getBody());
            return root.path("rates").path(toCurrency).asDouble();
        } else {
            throw new IOException("Failed to retrieve exchange rate");
        }
    }
}

