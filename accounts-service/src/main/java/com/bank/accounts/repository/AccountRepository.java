package com.bank.accounts.repository;

import com.bank.accounts.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByUserId(String userId);
    // Добавляем метод поиска по username
    Optional<Account> findByUsername(String username);
}