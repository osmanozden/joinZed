package com.zadApp.service;

import com.zadApp.model.Account;
import com.zadApp.model.User;
import com.zadApp.repository.IAccountRepository;
import com.zadApp.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final IUserRepository userRepository;
    private final IAccountRepository accountRepository;

    @Autowired
    public UserService(IUserRepository userRepository, IAccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public User createUserWithAccount(String name, double usdBalance, double tryBalance) {
        User user = new User();
        user.setName(name);
        User savedUser = userRepository.save(user);

        Account usdAccount = new Account();
        usdAccount.setUserId(savedUser.getId());
        usdAccount.setCurrency("USD");
        usdAccount.setBalance(usdBalance);
        accountRepository.save(usdAccount);

        Account tryAccount = new Account();
        tryAccount.setUserId(savedUser.getId());
        tryAccount.setCurrency("TRY");
        tryAccount.setBalance(tryBalance);
        accountRepository.save(tryAccount);

        return savedUser;
    }

    public long countUsers() {
        return userRepository.count();
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }


}
