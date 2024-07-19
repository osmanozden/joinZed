package com.zadApp.repository;

import com.zadApp.model.AccountModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IAccountRepository extends JpaRepository<AccountModel, Long> {
    Optional<AccountModel> findByUserIdAndCurrency(Long userId, String currency);
}
