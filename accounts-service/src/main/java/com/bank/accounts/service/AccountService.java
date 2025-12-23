package com.bank.accounts.service;

import com.bank.accounts.model.Account;
import com.bank.accounts.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private AccountRepository accountRepository;

    public Optional<Account> getAccountByUserId(String userId) {
        logger.info("Поиск аккаунта для пользователя: {}", userId);
        Optional<Account> accountOpt = accountRepository.findByUserId(userId);
        if (accountOpt.isPresent()) {
            logger.info("Аккаунт найден для пользователя: {}", userId);
        } else {
            logger.warn("Аккаунт не найден для пользователя: {}", userId);
        }
        return accountOpt;
    }

    public Account saveAccount(Account account) {
        logger.info("Сохранение аккаунта для пользователя: {}", account.getUserId());
        return accountRepository.save(account);
    }

    @Transactional
    public Account updateBalance(String userId, BigDecimal amount) {
        logger.info("Обновление баланса для пользователя {} на сумму {}", userId, amount);
        Optional<Account> accountOpt = accountRepository.findByUserId(userId);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            logger.info("Найден существующий аккаунт для пользователя {}: текущий баланс {}, добавляем {}", userId, account.getBalance(), amount);
            account.setBalance(account.getBalance().add(amount));
            Account savedAccount = accountRepository.save(account);
            logger.info("Баланс успешно обновлен для пользователя {}: новый баланс {}", userId, savedAccount.getBalance());
            return savedAccount;
        } else {
            logger.info("Аккаунт не найден для пользователя {}, создаем новый с балансом {}", userId, amount);
            // Если аккаунт не найден, создаем новый с нулевым балансом и добавляем к нему сумму
            Account newAccount = new Account();
            newAccount.setUserId(userId);
            newAccount.setFirstName("Unknown");
            newAccount.setLastName("Unknown");
            newAccount.setBirthDate(java.time.LocalDate.now().minusYears(25)); // по умолчанию 25 лет
            newAccount.setBalance(amount);
            Account savedAccount = accountRepository.save(newAccount);
            logger.info("Создан новый аккаунт для пользователя {}: баланс {}", userId, savedAccount.getBalance());
            return savedAccount;
        }
    }

    @Transactional
    public Account createAccount(String userId, String firstName, String lastName, String birthDate, BigDecimal initialBalance) {
        logger.info("Создание нового аккаунта для пользователя: {}", userId);
        // Проверка, что аккаунт с таким userId еще не существует
        if (accountRepository.findByUserId(userId).isPresent()) {
            logger.error("Аккаунт уже существует для пользователя: {}", userId);
            throw new RuntimeException("Account already exists for user ID: " + userId);
        }

        Account account = new Account();
        account.setUserId(userId);
        account.setFirstName(firstName);
        account.setLastName(lastName);
        account.setBirthDate(java.time.LocalDate.parse(birthDate));
        account.setBalance(initialBalance);

        Account savedAccount = accountRepository.save(account);
        logger.info("Аккаунт успешно создан для пользователя: {}", userId);
        return savedAccount;
    }

    @Transactional
    public Account updateAccount(String userId, String firstName, String lastName, String birthDate) {
        logger.info("Обновление данных аккаунта для пользователя: {}", userId);
        Optional<Account> accountOpt = accountRepository.findByUserId(userId);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            account.setFirstName(firstName);
            account.setLastName(lastName);
            account.setBirthDate(java.time.LocalDate.parse(birthDate));

            // Валидация возраста: пользователь должен быть старше 18 лет
            if (account.getBirthDate().isAfter(java.time.LocalDate.now().minusYears(18))) {
                logger.error("Пользователь младше 18 лет: {}", userId);
                throw new IllegalArgumentException("User must be older than 18 years");
            }

            Account savedAccount = accountRepository.save(account);
            logger.info("Данные аккаунта успешно обновлены для пользователя: {}", userId);
            return savedAccount;
        } else {
            logger.error("Аккаунт не найден для пользователя: {}", userId);
            throw new RuntimeException("Account not found for user ID: " + userId);
        }
    }
}