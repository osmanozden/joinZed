package com.zadApp.repository;

import com.zadApp.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IAccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUserIdAndCurrency(Long userId, String currency);
}
