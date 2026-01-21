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

    // Добавляем метод поиска по username
    public Optional<Account> getAccountByUsername(String username) {
        logger.info("Поиск аккаунта по username: {}", username);
        Optional<Account> accountOpt = accountRepository.findByUsername(username);
        if (accountOpt.isPresent()) {
            logger.info("Аккаунт найден для username: {}", username);
        } else {
            logger.warn("Аккаунт не найден для username: {}", username);
        }
        return accountOpt;
    }

    public Account saveAccount(Account account) {
        logger.info("Сохранение аккаунта для пользователя: {}", account.getUserId());
        return accountRepository.save(account);
    }

    @Transactional
    public Account updateBalance(String userId, String username, BigDecimal amount) {
        logger.info("Обновление баланса для пользователя {} с username {} на сумму {}", userId, username, amount);
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
            newAccount.setUsername(username != null ? username : userId); // Устанавливаем username из параметра или userId по умолчанию
            newAccount.setFirstName("Unknown");
            newAccount.setLastName("Unknown");
            newAccount.setBirthDate(java.time.LocalDate.now().minusYears(25)); // по умолчанию 25 лет
            newAccount.setBalance(amount);
            Account savedAccount = accountRepository.save(newAccount);
            logger.info("Создан новый аккаунт для пользователя {}: баланс {}, username {}", userId, savedAccount.getBalance(), savedAccount.getUsername());
            return savedAccount;
        }
    }

    @Transactional
    public Account createAccount(String userId, String username, String firstName, String lastName, String birthDate, BigDecimal initialBalance) {
        logger.info("Создание нового аккаунта для пользователя: {}", userId);
        // Проверка, что аккаунт с таким userId еще не существует
        if (accountRepository.findByUserId(userId).isPresent()) {
            logger.error("Аккаунт уже существует для пользователя: {}", userId);
            throw new RuntimeException("Account already exists for user ID: " + userId);
        }

        Account account = new Account();
        account.setUserId(userId);
        account.setUsername(username);
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
    
    // Новый метод для выполнения перевода между счетами
    @Transactional
    public boolean transferBetweenAccounts(String senderUserId, String recipientUserId, BigDecimal amount) {
        logger.info("Начало обработки перевода от {} к {}, сумма: {}", senderUserId, recipientUserId, amount);
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.error("Сумма перевода должна быть положительной: {}", amount);
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        
        if (senderUserId.equals(recipientUserId)) {
            logger.error("Нельзя выполнить перевод на тот же аккаунт: {}", senderUserId);
            throw new IllegalArgumentException("Sender and recipient cannot be the same user");
        }
        
        // Получаем аккаунты отправителя и получателя
        Optional<Account> senderAccountOpt = accountRepository.findByUserId(senderUserId);
        Optional<Account> recipientAccountOpt = accountRepository.findByUserId(recipientUserId);
        
        if (!senderAccountOpt.isPresent()) {
            logger.error("Аккаунт отправителя не найден: {}", senderUserId);
            return false;
        }
        
        if (!recipientAccountOpt.isPresent()) {
            logger.error("Аккаунт получателя не найден: {}", recipientUserId);
            return false;
        }
        
        Account senderAccount = senderAccountOpt.get();
        Account recipientAccount = recipientAccountOpt.get();
        
        logger.info("Проверка баланса отправителя: {} (требуется {}) для перевода", senderAccount.getBalance(), amount);
        
        // Проверяем, достаточно ли средств на счете отправителя
        if (senderAccount.getBalance().compareTo(amount) < 0) {
            logger.error("Недостаточно средств для перевода. Баланс отправителя: {}, сумма перевода: {}", 
                        senderAccount.getBalance(), amount);
            return false;
        }
        
        // Выполняем перевод: списание у отправителя и зачисление получателю
        logger.info("Выполнение перевода: списание {} у отправителя {} и зачисление получателю {}", 
                   amount, senderUserId, recipientUserId);
        senderAccount.setBalance(senderAccount.getBalance().subtract(amount));
        recipientAccount.setBalance(recipientAccount.getBalance().add(amount));
        
        // Сохраняем обновленные аккаунты
        accountRepository.save(senderAccount);
        accountRepository.save(recipientAccount);
        
        logger.info("Перевод успешно выполнен: {} -> {}, сумма: {}", senderUserId, recipientUserId, amount);
        logger.info("Новый баланс отправителя: {}", senderAccount.getBalance());
        logger.info("Новый баланс получателя: {}", recipientAccount.getBalance());
        
        return true;
    }
    
    // Новый метод для выполнения перевода между счетами по username получателя
    @Transactional
    public boolean transferBetweenAccountsByUsername(String senderUserId, String recipientUsername, BigDecimal amount) {
        logger.info("Начало обработки перевода от {} к пользователю с username {}, сумма: {}", senderUserId, recipientUsername, amount);
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.error("Сумма перевода должна быть положительной: {}", amount);
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        
        // Получаем аккаунт отправителя
        Optional<Account> senderAccountOpt = accountRepository.findByUserId(senderUserId);
        if (!senderAccountOpt.isPresent()) {
            logger.error("Аккаунт отправителя не найден: {}", senderUserId);
            return false;
        }
        
        // Получаем аккаунт получателя по username
        Optional<Account> recipientAccountOpt = accountRepository.findByUsername(recipientUsername);
        if (!recipientAccountOpt.isPresent()) {
            logger.error("Аккаунт получателя не найден по username: {}", recipientUsername);
            return false;
        }
        
        Account senderAccount = senderAccountOpt.get();
        Account recipientAccount = recipientAccountOpt.get();
        
        // Проверяем, что отправитель и получатель не являются одним и тем же пользователем
        if (senderAccount.getUserId().equals(recipientAccount.getUserId())) {
            logger.error("Нельзя выполнить перевод на тот же аккаунт: {} и {}", senderUserId, recipientAccount.getUserId());
            throw new IllegalArgumentException("Sender and recipient cannot be the same user");
        }
        
        logger.info("Проверка баланса отправителя: {} (требуется {}) для перевода", senderAccount.getBalance(), amount);
        
        // Проверяем, достаточно ли средств на счете отправителя
        if (senderAccount.getBalance().compareTo(amount) < 0) {
            logger.error("Недостаточно средств для перевода. Баланс отправителя: {}, сумма перевода: {}", 
                        senderAccount.getBalance(), amount);
            return false;
        }
        
        // Выполняем перевод: списание у отправителя и зачисление получателю
        logger.info("Выполнение перевода: списание {} у отправителя {} и зачисление получателю с username {} (ID: {})", 
                   amount, senderUserId, recipientUsername, recipientAccount.getUserId());
        senderAccount.setBalance(senderAccount.getBalance().subtract(amount));
        recipientAccount.setBalance(recipientAccount.getBalance().add(amount));
        
        // Сохраняем обновленные аккаунты
        accountRepository.save(senderAccount);
        accountRepository.save(recipientAccount);
        
        logger.info("Перевод успешно выполнен: {} -> {} (username: {}), сумма: {}", 
                   senderUserId, recipientAccount.getUserId(), recipientUsername, amount);
        logger.info("Новый баланс отправителя: {}", senderAccount.getBalance());
        logger.info("Новый баланс получателя: {}", recipientAccount.getBalance());
        
        return true;
    }
}